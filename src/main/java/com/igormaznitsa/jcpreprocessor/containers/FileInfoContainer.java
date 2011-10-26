package com.igormaznitsa.jcpreprocessor.containers;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.directives.AbstractDirectiveHandler;
import com.igormaznitsa.jcpreprocessor.directives.DirectiveBehaviour;
import com.igormaznitsa.jcpreprocessor.exceptions.PreprocessorException;
import com.igormaznitsa.jcpreprocessor.removers.JavaCommentsRemover;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class FileInfoContainer {

    private final File sourceFile;
    private final boolean forCopyOnly;
    private boolean excludedFromPreprocessing;
    private String destinationDir;
    private String destinationName;

    public File getSourceFile() {
        return sourceFile;
    }

    public boolean isExcludedFromPreprocessing() {
        return excludedFromPreprocessing;
    }

    public boolean isForCopyOnly() {
        return forCopyOnly;
    }

    public String getDestinationDir() {
        return destinationDir;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public FileInfoContainer(final File srcFile, final String dstFileName, final boolean copingOnly) {
        forCopyOnly = copingOnly;
        excludedFromPreprocessing = false;
        sourceFile = srcFile;

        int dirSeparator = dstFileName.lastIndexOf('/');
        if (dirSeparator < 0) {
            dirSeparator = dstFileName.lastIndexOf('\\');
        }

        if (dirSeparator < 0) {
            destinationDir = "." + File.separatorChar;
            destinationName = dstFileName;
        } else {
            destinationDir = dstFileName.substring(0, dirSeparator);
            destinationName = dstFileName.substring(dirSeparator);
        }
    }

    public String getDestinationFilePath() {
        return destinationDir + File.separatorChar + destinationName;
    }

    @Override
    public String toString() {
        return sourceFile.getAbsolutePath();
    }

    private boolean isGlobalOperation(final String str) {
        return str.startsWith("//#_if") || str.startsWith("//#_else") || str.startsWith("//#_endif") || str.startsWith("//#global") || str.startsWith("//#exclude");
    }

    public void preprocess(final PreprocessorContext configurator) throws PreprocessorException, IOException {
        configurator.clearLocalVariables();

        final ParameterContainer paramContainer = new ParameterContainer(this, configurator.getCharacterEncoding());
        preprocess(paramContainer, configurator);

        final File outFile = configurator.makeDestinationFile(getDestinationFilePath());
        paramContainer.saveBuffersToFile(outFile);
    }

    private void printSpaces(final ParameterContainer paramContainer, final int number) throws IOException {
        for (int li = 0; li < number; li++) {
            paramContainer.getPrinter().print(" ");
        }
    }

    public void preprocess(final ParameterContainer paramContainer, final PreprocessorContext configurator) throws PreprocessorException {
        String trimmedProcessingString = null;
        try {
            while (true) {
                String nonTrimmedProcessingString = paramContainer.nextLine();
                if (paramContainer.getState().contains(PreprocessingState.END_PROCESSING)) {
                    nonTrimmedProcessingString = null;
                }

                if (nonTrimmedProcessingString == null) {
                    if (!paramContainer.isOnlyRootOnStack()) {
                        paramContainer.popTextContainer();
                        continue;
                    } else {
                        break;
                    }
                }

                trimmedProcessingString = nonTrimmedProcessingString.trim();

                final int numberOfSpacesAtTheLineBeginning = nonTrimmedProcessingString.indexOf(trimmedProcessingString);

                String stringToBeProcessed = trimmedProcessingString;
                if (!trimmedProcessingString.startsWith("//$$")) {
                    stringToBeProcessed = PreprocessorUtils.processMacros(trimmedProcessingString, configurator);
                }

                if (isGlobalOperation(stringToBeProcessed)) {
                    continue;
                }

                switch (processDirective(paramContainer, stringToBeProcessed, configurator)) {
                    case READ_NEXT_LINE:
                        continue;
                    case PROCESSED: {
                    }
                    break;
                    case NOT_PROCESSED: {
                        if (paramContainer.isDirectiveCanBeProcessed() && !paramContainer.getState().contains(PreprocessingState.TEXT_OUTPUT_DISABLED)) {
                            if (stringToBeProcessed.startsWith("//$$")) {
                                // Output the tail of the string to the output stream without comments and macroses
                                printSpaces(paramContainer, numberOfSpacesAtTheLineBeginning);
                                paramContainer.getPrinter().println(PreprocessorUtils.extractTail("//$$", trimmedProcessingString));
                            } else if (stringToBeProcessed.startsWith("//$")) {
                                // Output the tail of the string to the output stream without comments
                                printSpaces(paramContainer, numberOfSpacesAtTheLineBeginning);
                                paramContainer.getPrinter().println(PreprocessorUtils.extractTail("//$", stringToBeProcessed));
                            } else {
                                // Just string :)
                                final String strToOut = processStringForTailRemover(stringToBeProcessed);

                                if (paramContainer.getState().contains(PreprocessingState.COMMENT_NEXT_LINE)) {
                                    paramContainer.getPrinter().print("//");
                                    paramContainer.getState().remove(PreprocessingState.COMMENT_NEXT_LINE);
                                }

                                printSpaces(paramContainer, numberOfSpacesAtTheLineBeginning);
                                paramContainer.getPrinter().println(stringToBeProcessed);
                            }
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            throw new PreprocessorException("Exception during preprocessing [" + e.getMessage() + "][" + paramContainer.getFileIncludeStackAsString() + ']',
                    paramContainer.getRootFileInfo().getSourceFile(),
                    paramContainer.peekFile().getFile(),
                    trimmedProcessingString,
                    paramContainer.peekFile().getNextStringIndex(), e);
        }

        if (!paramContainer.isIfStackEmpty()) {
            throw new PreprocessorException("Unclosed #if instruction",
                    paramContainer.getRootFileInfo().getSourceFile(),
                    paramContainer.peekIf().getFile(), null, paramContainer.peekIf().getNextStringIndex(), null);
        }
        if (!paramContainer.isWhileStackEmpty()) {
            throw new PreprocessorException("Unclosed #when instruction",
                    paramContainer.getRootFileInfo().getSourceFile(),
                    paramContainer.peekWhile().getFile(), null, paramContainer.peekWhile().getNextStringIndex(), null);
        }


    }

    private static String processStringForTailRemover(final String str) {
        final int tailRemoverStart = str.indexOf("/*-*/");
        if (tailRemoverStart >= 0) {
            return str.substring(0, tailRemoverStart);
        }
        return str;
    }

    protected DirectiveBehaviour processDirective(final ParameterContainer state, final String string, final PreprocessorContext configurator) throws IOException {
        if (string.startsWith("//#")) {
            final String tail = PreprocessorUtils.extractTail("//#", string);
            final boolean executionIsEnabled = state.isDirectiveCanBeProcessed();

            for (final AbstractDirectiveHandler handler : AbstractDirectiveHandler.DIRECTIVES) {
                final String name = handler.getName();
                if (tail.startsWith(name)) {
                    final boolean allowedForExecution = executionIsEnabled || !handler.processOnlyIfCanBeProcessed();
                    
                    final String restOfString = PreprocessorUtils.extractTail(name, tail);
                    if (handler.hasExpression()) {
                        if (!restOfString.isEmpty() && Character.isSpaceChar(restOfString.charAt(0))) {
                            if (allowedForExecution) {
                                return handler.execute(restOfString.trim(), state, configurator);
                            } else {
                                return DirectiveBehaviour.PROCESSED;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        if (allowedForExecution) {
                            return handler.execute(restOfString.trim(), state, configurator);
                        } else {
                            return DirectiveBehaviour.PROCESSED;
                        }
                    }
                }
            }
            throw new RuntimeException("Unknown preprocessor directive [" + string + ']');
        }

        return DirectiveBehaviour.NOT_PROCESSED;
    }

    private final void removeCommentsFromFile(final File file, final PreprocessorContext cfg) throws IOException {
        int len = (int) file.length();
        int pos = 0;
        final byte[] memoryFile = new byte[len];
        FileInputStream inStream = new FileInputStream(file);
        try {
            while (len > 0) {
                final int read = inStream.read(memoryFile, pos, len);
                if (read < 0) {
                    break;
                }
                pos += read;
                len -= read;
            }

            if (len > 0) {
                throw new IOException("Wrong read length");
            }
        } finally {
            try {
                inStream.close();
            } catch (IOException ex) {
            }
        }

        if (!file.delete()) {
            throw new IOException("Can't delete the file " + file.getAbsolutePath());
        }

        final Reader reader = new InputStreamReader(new ByteArrayInputStream(memoryFile), cfg.getCharacterEncoding());

        final FileWriter writer = new FileWriter(file, false);
        try {
            new JavaCommentsRemover(reader, writer).process();
            writer.flush();
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
            }
        }
    }

    public void setDestinationDir(final String stringToBeProcessed) {
        destinationDir = stringToBeProcessed;
    }

    public void setDestinationName(String stringToBeProcessed) {
        destinationName = stringToBeProcessed;
    }

    public void setExcluded(final boolean flag) {
        excludedFromPreprocessing = flag;
    }
}
