/*
 * Copyright 2011 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307  USA
 */
package com.igormaznitsa.jcpreprocessor.context;

import com.igormaznitsa.jcpreprocessor.containers.FileInfoContainer;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.containers.TextFileDataContainer;
import com.igormaznitsa.jcpreprocessor.exceptions.FilePositionInfo;
import com.igormaznitsa.jcpreprocessor.exceptions.PreprocessorException;
import com.igormaznitsa.jcpreprocessor.removers.JavaCommentsRemover;
import com.igormaznitsa.jcpreprocessor.utils.ResetablePrinter;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The class describes a preprocessor state also it contains inside buffers and save data on disk
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class PreprocessingState {

    public static class ExcludeIfInfo {

        private final FileInfoContainer fileInfoContainer;
        private final String condition;
        private final int stringIndex;

        public ExcludeIfInfo(final FileInfoContainer fileInfoContainer, final String condition, final int stringIndex) {
            this.fileInfoContainer = fileInfoContainer;
            this.condition = condition;
            this.stringIndex = stringIndex;
        }

        public int getStringIndex() {
            return this.stringIndex;
        }

        public FileInfoContainer getFileInfoContainer() {
            return fileInfoContainer;
        }

        public String getCondition() {
            return condition;
        }
    }

    public enum PrinterType {

        NORMAL,
        PREFIX,
        POSTFIX
    }
    private final String globalInCharacterEncoding;
    private final String globalOutCharacterEncoding;
    private final TextFileDataContainer rootReference;
    private final FileInfoContainer rootFileInfo;
    private final LinkedList<TextFileDataContainer> whileStack = new LinkedList<TextFileDataContainer>();
    private final LinkedList<TextFileDataContainer> ifStack = new LinkedList<TextFileDataContainer>();
    private final LinkedList<TextFileDataContainer> fileStack = new LinkedList<TextFileDataContainer>();
    private final LinkedList<ExcludeIfInfo> excludeStack = new LinkedList<ExcludeIfInfo>();
    private final ResetablePrinter prefixPrinter = new ResetablePrinter(1024);
    private final ResetablePrinter postfixPrinter = new ResetablePrinter(64 * 1024);
    private final ResetablePrinter normalPrinter = new ResetablePrinter(1024);
    private ResetablePrinter currentPrinter;
    private final EnumSet<PreprocessingFlag> preprocessingFlags = EnumSet.noneOf(PreprocessingFlag.class);
    private TextFileDataContainer activeIf;
    private TextFileDataContainer activeWhile;

    PreprocessingState(final FileInfoContainer rootFile, final String inEncoding, final String outEncoding) throws IOException {
        if (rootFile == null) {
            throw new NullPointerException("The root file is null");
        }

        if (inEncoding == null) {
            throw new NullPointerException("InEncoding is null");
        }

        if (outEncoding == null) {
            throw new NullPointerException("OutEncoding is null");
        }

        this.globalInCharacterEncoding = inEncoding;
        this.globalOutCharacterEncoding = outEncoding;

        this.rootFileInfo = rootFile;
        init();
        rootReference = openFile(rootFile.getSourceFile());
    }

    PreprocessingState(final FileInfoContainer rootFile, final TextFileDataContainer rootContainer, final String inEncoding, final String outEncoding) throws IOException {
        if (rootFile == null) {
            throw new NullPointerException("The root file is null");
        }

        if (inEncoding == null) {
            throw new NullPointerException("InEncoding is null");
        }

        this.globalInCharacterEncoding = inEncoding;
        this.globalOutCharacterEncoding = outEncoding;

        this.rootFileInfo = rootFile;
        init();
        rootReference = rootContainer;
        fileStack.push(rootContainer);
    }

    public void pushExcludeIfData(final FileInfoContainer infoContainer, final String excludeIfCondition, final int stringIndex) {
        if (infoContainer == null) {
            throw new NullPointerException("File info is null");
        }

        if (excludeIfCondition == null) {
            throw new NullPointerException("Condition is null");
        }

        if (stringIndex < 0) {
            throw new IllegalArgumentException("String index is less than zero");
        }

        excludeStack.push(new ExcludeIfInfo(infoContainer, excludeIfCondition, stringIndex));
    }

    public List<ExcludeIfInfo> popAllExcludeIfInfoData() {
        final List<ExcludeIfInfo> result = new ArrayList<ExcludeIfInfo>(excludeStack);
        excludeStack.clear();
        return result;
    }

    public ExcludeIfInfo popExcludeIfData() {
        return excludeStack.pop();
    }

    public Set<PreprocessingFlag> getPreprocessingFlags() {
        return preprocessingFlags;
    }

    public ResetablePrinter getPrinter() throws IOException {
        return currentPrinter;
    }

    public TextFileDataContainer getRootTextContainer() {
        return rootReference;
    }

    public TextFileDataContainer openFile(final File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("The file is null");
        }

        final String[] texts = PreprocessorUtils.readWholeTextFileIntoArray(file, globalInCharacterEncoding);
        final TextFileDataContainer newContainer = new TextFileDataContainer(file, texts, 0);
        fileStack.push(newContainer);
        return newContainer;
    }

    public TextFileDataContainer peekFile() {
        return fileStack.peek();
    }

    public TextFileDataContainer popTextContainer() {
        if (fileStack.size() == 1) {
            throw new IllegalStateException("Attemption to remove the root file");
        } else {
            return fileStack.pop();
        }
    }

    public FileInfoContainer getRootFileInfo() {
        return rootFileInfo;
    }

    public boolean isOnlyRootOnStack() {
        return fileStack.size() == 1;
    }

    private TextFileDataContainer cloneTopTextDataContainer(final boolean useLastReadStringIndex) {
        final TextFileDataContainer topElement = fileStack.peek();
        return new TextFileDataContainer(topElement, useLastReadStringIndex ? topElement.getLastReadStringIndex() : topElement.getNextStringIndex());
    }

    public PreprocessingState popWhile() {
        final TextFileDataContainer whileOnTop = whileStack.pop();
        if (whileOnTop == activeWhile) {
            preprocessingFlags.remove(PreprocessingFlag.BREAK_COMMAND);
            if (whileStack.isEmpty()) {
                activeWhile = null;
            } else {
                activeWhile = whileStack.peek();
            }
        }
        return this;
    }

    public PreprocessingState pushWhile(final boolean makeActive) {
        final TextFileDataContainer whileRef = cloneTopTextDataContainer(true);
        whileStack.push(whileRef);
        if (makeActive) {
            activeWhile = whileRef;
        }
        return this;
    }

    public TextFileDataContainer peekWhile() {
        return whileStack.peek();
    }

    public String nextLine() {
        return fileStack.peek().nextLine();
    }

    public PreprocessingState goToString(final int stringIndex) {
        fileStack.peek().setNextStringIndex(stringIndex);
        return this;
    }

    public PreprocessingState pushIf(final boolean makeActive) {
        final TextFileDataContainer ifRef = cloneTopTextDataContainer(true);
        ifStack.push(ifRef);
        if (makeActive) {
            activeIf = ifRef;
        }
        return this;
    }

    public void popAllIFUntilContainerWithFile(final TextFileDataContainer container) {
        final File file = container.getFile();
        final int stringIndex = container.getNextStringIndex();
        while (!ifStack.isEmpty()) {
            final TextFileDataContainer top = ifStack.peek();
            if (!top.getFile().equals(file) || top.getNextStringIndex() <= stringIndex) {
                break;
            } else {
                ifStack.pop();
            }
        }
    }

    public PreprocessingState popIf() {
        final TextFileDataContainer ifRef = ifStack.pop();
        if (ifRef == activeIf) {
            if (ifStack.isEmpty()) {
                activeIf = null;
            } else {
                activeIf = ifStack.peek();
            }
        }
        return this;
    }

    public boolean isAtActiveWhile() {
        if (whileStack.isEmpty()) {
            return true;
        } else {
            return activeWhile == whileStack.peek();
        }
    }

    public boolean isAtActiveIf() {
        if (ifStack.isEmpty()) {
            return true;
        } else {
            return ifStack.peek() == activeIf;
        }
    }

    public boolean isDirectiveCanBeProcessedIgnoreBreak() {
        return isAtActiveIf() && isAtActiveWhile() && !preprocessingFlags.contains(PreprocessingFlag.IF_CONDITION_FALSE);
    }

    public boolean isDirectiveCanBeProcessed() {
        return isDirectiveCanBeProcessedIgnoreBreak() && !preprocessingFlags.contains(PreprocessingFlag.BREAK_COMMAND);
    }

    public TextFileDataContainer peekIf() {
        return ifStack.peek();
    }

    public boolean isIfStackEmpty() {
        return ifStack.isEmpty();
    }

    public boolean isWhileStackEmpty() {
        return whileStack.isEmpty();
    }

    private void init() throws IOException {
        preprocessingFlags.clear();
        resetPrinters();

        setPrinter(PrinterType.NORMAL);
    }

    public void setPrinter(final PrinterType type) {
        if (type == null) {
            throw new NullPointerException("Type is null");
        }
        switch (type) {
            case NORMAL:
                currentPrinter = normalPrinter;
                break;
            case POSTFIX:
                currentPrinter = postfixPrinter;
                break;
            case PREFIX:
                currentPrinter = prefixPrinter;
                break;
            default:
                throw new IllegalArgumentException("Unsupported type detected [" + type.name() + ']');
        }
    }

    public void resetPrinters() throws IOException {
        normalPrinter.reset();
        prefixPrinter.reset();
        postfixPrinter.reset();

        currentPrinter = normalPrinter;
    }

    public void saveBuffersToStreams(final OutputStream prefix, final OutputStream normal, final OutputStream postfix) throws IOException {
        prefixPrinter.writeBufferTo(new BufferedWriter(new OutputStreamWriter(prefix, globalOutCharacterEncoding)));
        normalPrinter.writeBufferTo(new BufferedWriter(new OutputStreamWriter(prefix, globalOutCharacterEncoding)));
        postfixPrinter.writeBufferTo(new BufferedWriter(new OutputStreamWriter(prefix, globalOutCharacterEncoding)));
    }

    public void saveBuffersToFile(final File outFile, final boolean removeComments) throws IOException {
        final File path = outFile.getParentFile();

        if (path != null && !path.exists() && !path.mkdirs()) {
            throw new IOException("Can't make directory [" + PreprocessorUtils.getFilePath(path) + ']');
        }

        Writer writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outFile, false), 16384), globalOutCharacterEncoding);

        try {
            if (removeComments) {
                writer = new StringWriter(prefixPrinter.getSize()+normalPrinter.getSize()+postfixPrinter.getSize());
                writePrinterBuffers(writer);
                final String source = writer.toString();
                
                writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outFile, false), 16384), globalOutCharacterEncoding);
                new JavaCommentsRemover(new StringReader(source), writer).process();
            } else {
                writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outFile, false), 16384), globalOutCharacterEncoding);
                writePrinterBuffers(writer);
            }
        } finally {
            PreprocessorUtils.closeSilently(writer);
        }
    }

    private void writePrinterBuffers(final Writer writer) throws IOException {
        if (!prefixPrinter.isEmpty()) {
            prefixPrinter.writeBufferTo(writer);
        }

        if (!normalPrinter.isEmpty()) {
            normalPrinter.writeBufferTo(writer);
        }

        if (!postfixPrinter.isEmpty()) {
            postfixPrinter.writeBufferTo(writer);
        }
    }

    public PreprocessorException makeException(final String message, final String text, final Throwable cause) {
        final FilePositionInfo[] stack = new FilePositionInfo[fileStack.size()];
        for (int i = 0; i < fileStack.size(); i++) {
            final TextFileDataContainer fileContainer = fileStack.get(i);
            stack[i] = new FilePositionInfo(fileContainer.getFile(), fileContainer.getLastReadStringIndex());
        }

        return new PreprocessorException(message, text, stack, cause);
    }
}
