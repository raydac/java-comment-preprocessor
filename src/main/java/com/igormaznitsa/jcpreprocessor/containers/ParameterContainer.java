package com.igormaznitsa.jcpreprocessor.containers;

import com.igormaznitsa.jcpreprocessor.utils.ResetablePrinter;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class ParameterContainer {

    public static class ExcludeIfInfo{
        private final FileInfoContainer fileInfoContainer;
        private final String condition;
        private final int stringIndex;
        
        public ExcludeIfInfo(final FileInfoContainer fileInfoContainer, final String condition, final int stringIndex){
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
        
        public String getCondition(){
            return condition;
        }
    }
    
    public enum PrinterType {
        NORMAL,
        PREFIX,
        POSTFIX
    }
    
    private final String globalCharacterEncoding;
    
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
    private final EnumSet<PreprocessingState> insideState = EnumSet.noneOf(PreprocessingState.class);
    private TextFileDataContainer activeIf;
    private TextFileDataContainer activeWhile;
    
    public ParameterContainer(final FileInfoContainer rootFile, final String encoding) throws IOException {
        if (rootFile == null){
            throw new NullPointerException("The root file is null");
        }
        
        if (encoding == null) {
            throw new NullPointerException("Encoding is null");
        }
        
        this.globalCharacterEncoding = encoding;
        this.rootFileInfo = rootFile;
        init();
        rootReference = openFile(rootFile.getSourceFile());
    }

    public ParameterContainer(final FileInfoContainer rootFile, final TextFileDataContainer rootContainer, final String encoding) throws IOException {
        if (rootFile == null){
            throw new NullPointerException("The root file is null");
        }
        
        if (encoding == null) {
            throw new NullPointerException("Encoding is null");
        }
        
        this.globalCharacterEncoding = encoding;
        this.rootFileInfo = rootFile;
        init();
        rootReference = rootContainer;
        fileStack.push(rootContainer);
    }

    public void pushExcludeIfData(final FileInfoContainer infoContainer, final String excludeIfCondition, final int stringIndex){
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
    
    public Set<PreprocessingState> getState() {
        return insideState;
    }

    public ResetablePrinter getPrinter() throws IOException {
        return currentPrinter;
    }

    public TextFileDataContainer getRootTextContainer() {
        return rootReference;
    }

    public TextFileDataContainer openFile(final File file) throws IOException {
        final String[] texts = PreprocessorUtils.readWholeTextFileIntoArray(file, globalCharacterEncoding);
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

    private TextFileDataContainer cloneFileOnTop(final boolean decreaseStringIndex) {
        final TextFileDataContainer topElement = fileStack.peek();
        return new TextFileDataContainer(topElement, decreaseStringIndex ? (topElement.getNextStringIndex()-1) : topElement.getNextStringIndex());
    }

    public ParameterContainer popWhile() {
        final TextFileDataContainer whileOnTop = whileStack.pop();
        if (whileOnTop == activeWhile) {
            insideState.remove(PreprocessingState.BREAK_COMMAND);
            if (whileStack.isEmpty()) {
                activeWhile = null;
            } else {
                activeWhile = whileStack.peek();
            }
        }
        return this;
    }

    public ParameterContainer pushWhile(final boolean makeActive) {
        final TextFileDataContainer whileRef = cloneFileOnTop(true);
        whileStack.push(whileRef);
        if (makeActive){
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

    public ParameterContainer goToString(final int stringIndex) {
        fileStack.peek().setNextStringIndex(stringIndex);
        return this;
    }

    public ParameterContainer pushIf(final boolean makeActive) {
        final TextFileDataContainer ifRef = cloneFileOnTop(true);
        ifStack.push(ifRef);
        if (makeActive) {
            activeIf = ifRef;
        }
        return this;
    }

    public void popAllIfUntil(final TextFileDataContainer container) {
        final File file = container.getFile();
        final int stringIndex = container.getNextStringIndex();
        while(!ifStack.isEmpty()){
            final TextFileDataContainer top = ifStack.peek();
            if (!top.getFile().equals(file) || top.getNextStringIndex()<=stringIndex){
                break;
            } else {
                ifStack.pop();
            }
        }
    }
    
    public ParameterContainer popIf() {
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

    public boolean isAtActiveWhile(){
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
        return isAtActiveIf() && isAtActiveWhile() && !insideState.contains(PreprocessingState.IF_CONDITION_FALSE);
    }
    
    public boolean isDirectiveCanBeProcessed() {
        return isDirectiveCanBeProcessedIgnoreBreak() && !insideState.contains(PreprocessingState.BREAK_COMMAND);
    }
    
    public TextFileDataContainer peekIf(){
        return ifStack.peek();
    }
    
    public boolean isIfStackEmpty() {
        return ifStack.isEmpty();
    }

    public boolean isWhileStackEmpty() {
        return whileStack.isEmpty();
    }

    private void init() throws IOException {
        insideState.clear();
        resetPrinters();

        setPrinter(PrinterType.NORMAL);
    }

    public void setPrinter(PrinterType type) {
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
                throw new IllegalArgumentException("Unsupported type detected");
        }
    }

    public void resetPrinters() throws IOException {
        normalPrinter.reset();
        prefixPrinter.reset();
        postfixPrinter.reset();

        currentPrinter = normalPrinter;
    }

    public void saveBuffersToStreams(final OutputStream prefix, final OutputStream normal, final OutputStream postfix) throws IOException {
        prefixPrinter.write(new BufferedWriter(new OutputStreamWriter(prefix, globalCharacterEncoding)));
        normalPrinter.write(new BufferedWriter(new OutputStreamWriter(prefix, globalCharacterEncoding)));
        postfixPrinter.write(new BufferedWriter(new OutputStreamWriter(prefix, globalCharacterEncoding)));
    }

    public String getFileIncludeStackAsString(){
        final StringBuilder result = new StringBuilder();
        
        final Iterator<TextFileDataContainer> reverse = fileStack.descendingIterator();
        while(reverse.hasNext()){
            final TextFileDataContainer container = reverse.next();
            if (result.length()>0){
                result.append("->");
            }
            result.append(container.getFile().getAbsolutePath());
        }
        
        return result.toString();
    }
    
    public void saveBuffersToFile(final File outFile) throws IOException {
        final File path = outFile.getParentFile();
        
        if (path != null && !path.exists() && !path.mkdirs()) {
                throw new IOException("Can't make directory [" + path.getAbsolutePath() + ']');
        }

        final BufferedWriter writer = new BufferedWriter(new FileWriter(outFile,true));
        try {
            if (!prefixPrinter.isEmpty()) {
                prefixPrinter.write(writer);
            }

            if (!normalPrinter.isEmpty()) {
                normalPrinter.write(writer);
            }

            if (!postfixPrinter.isEmpty()) {
                postfixPrinter.write(writer);
            }
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
            }
        }
    }
}
