package com.igormaznitsa.jcpreprocessor.containers;

import com.igormaznitsa.jcpreprocessor.context.JCPSpecialVariables;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.directives.AbstractDirectiveHandler;
import com.igormaznitsa.jcpreprocessor.directives.AfterProcessingBehaviour;
import com.igormaznitsa.jcpreprocessor.directives.DirectiveArgumentType;
import com.igormaznitsa.jcpreprocessor.exceptions.FilePositionInfo;
import com.igormaznitsa.jcpreprocessor.exceptions.PreprocessorException;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.removers.JavaCommentsRemover;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

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

    private void printSpaces(final PreprocessingState paramContainer, final int number) throws IOException {
        for (int li = 0; li < number; li++) {
            paramContainer.getPrinter().print(" ");
        }
    }

    public List<PreprocessingState.ExcludeIfInfo> processGlobalDirectives(final PreprocessingState state, final PreprocessorContext context) throws PreprocessorException, IOException {
        final PreprocessingState preprocessingState = state == null ? new PreprocessingState(this, context.getCharacterEncoding()) : state;

        String trimmedProcessingString = null;
        try {
            while (true) {
                String nonTrimmedProcessingString = preprocessingState.nextLine();
                if (preprocessingState.getPreprocessingFlags().contains(PreprocessingFlag.END_PROCESSING)) {
                    nonTrimmedProcessingString = null;
                }

                if (nonTrimmedProcessingString == null) {
                    if (!preprocessingState.isOnlyRootOnStack()) {
                        preprocessingState.popTextContainer();
                        continue;
                    } else {
                        break;
                    }
                }

                trimmedProcessingString = nonTrimmedProcessingString.trim();

                final int numberOfSpacesAtTheLineBeginning = nonTrimmedProcessingString.indexOf(trimmedProcessingString);

                if (trimmedProcessingString.startsWith(AbstractDirectiveHandler.DIRECTIVE_PREFIX)) {
                    switch (processDirective(preprocessingState, PreprocessorUtils.extractTail(AbstractDirectiveHandler.DIRECTIVE_PREFIX, trimmedProcessingString), context,true)) {
                        case PROCESSED:
                        case READ_NEXT_LINE:
                            continue;
                        default:
                            throw new Error("Unsupported result");
                    }
                }
            }
        } catch (Exception unexpected) {
            throw preprocessingState.makeException("Unexpected exception detected", trimmedProcessingString, unexpected);
        }

        if (!preprocessingState.isIfStackEmpty()) {
           final TextFileDataContainer lastIf = preprocessingState.peekIf();
            throw new PreprocessorException("Unclosed " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + "_if instruction detected",
                    "", new FilePositionInfo[]{new FilePositionInfo(lastIf.getFile(), lastIf.getNextStringIndex())}, null);
        }
        
        return preprocessingState.popAllExcludeIfInfoData();
    }
    
    public PreprocessingState preprocessFile(final PreprocessingState state, final PreprocessorContext context) throws IOException, PreprocessorException {
        context.clearLocalVariables();
        final PreprocessingState preprocessingState = state != null ? state : new PreprocessingState(this, context.getCharacterEncoding());
        
        String trimmedProcessingString = null;
        try {
            while (true) {
                String nonTrimmedProcessingString = preprocessingState.nextLine();
                if (preprocessingState.getPreprocessingFlags().contains(PreprocessingFlag.END_PROCESSING)) {
                    nonTrimmedProcessingString = null;
                }

                if (nonTrimmedProcessingString == null) {
                    if (!preprocessingState.isOnlyRootOnStack()) {
                        preprocessingState.popTextContainer();
                        continue;
                    } else {
                        break;
                    }
                }

                trimmedProcessingString = nonTrimmedProcessingString.trim();
                
                final int numberOfSpacesAtTheLineBeginning = nonTrimmedProcessingString.indexOf(trimmedProcessingString);

                String stringToBeProcessed = trimmedProcessingString;

                if (stringToBeProcessed.startsWith(AbstractDirectiveHandler.DIRECTIVE_PREFIX)) {
                    switch (processDirective(preprocessingState, PreprocessorUtils.extractTail(AbstractDirectiveHandler.DIRECTIVE_PREFIX, stringToBeProcessed), context,false)) {
                        case PROCESSED:
                        case READ_NEXT_LINE:
                            continue;
                        default:
                            throw new Error("Unsupported result");
                    }
                }

                if (preprocessingState.isDirectiveCanBeProcessed() && !preprocessingState.getPreprocessingFlags().contains(PreprocessingFlag.TEXT_OUTPUT_DISABLED)) {
                    final boolean startsWithTwoDollars = trimmedProcessingString.startsWith("//$$");
    
                    if (!startsWithTwoDollars) {
                        stringToBeProcessed = PreprocessorUtils.processMacroses(trimmedProcessingString, context,preprocessingState);
                    }

                    if (startsWithTwoDollars) {
                        // Output the tail of the string to the output stream without comments and macroses
                        printSpaces(preprocessingState, numberOfSpacesAtTheLineBeginning);
                        preprocessingState.getPrinter().println(PreprocessorUtils.extractTail("//$$", trimmedProcessingString));
                    } else if (stringToBeProcessed.startsWith("//$")) {
                        // Output the tail of the string to the output stream without comments
                        printSpaces(preprocessingState, numberOfSpacesAtTheLineBeginning);
                        preprocessingState.getPrinter().println(PreprocessorUtils.extractTail("//$", stringToBeProcessed));
                    } else {
                        // Just string :)
                        final String strToOut = processStringForTailRemover(stringToBeProcessed);

                        if (preprocessingState.getPreprocessingFlags().contains(PreprocessingFlag.COMMENT_NEXT_LINE)) {
                            preprocessingState.getPrinter().print("//");
                            preprocessingState.getPreprocessingFlags().remove(PreprocessingFlag.COMMENT_NEXT_LINE);
                        }

                        printSpaces(preprocessingState, numberOfSpacesAtTheLineBeginning);
                        preprocessingState.getPrinter().println(stringToBeProcessed);
                    }
                }

            }
        } catch (RuntimeException unexpected) {
            throw state.makeException("Unexpected exception detected", trimmedProcessingString, unexpected);
        }

        if (!preprocessingState.isIfStackEmpty()) {
            final TextFileDataContainer lastIf = preprocessingState.peekIf();
            throw new PreprocessorException("Unclosed " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + "if instruction detected",
                    "", new FilePositionInfo[]{new FilePositionInfo(lastIf.getFile(), lastIf.getNextStringIndex())}, null);
        }
        if (!preprocessingState.isWhileStackEmpty()) {
            final TextFileDataContainer lastWhile = preprocessingState.peekWhile();
            throw new PreprocessorException("Unclosed " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + "while instruction detected",
                    "", new FilePositionInfo[]{new FilePositionInfo(lastWhile.getFile(), lastWhile.getNextStringIndex())}, null);
        }

        if (!context.isFileOutputDisabled()){
            final File outFile = context.makeDestinationFile(getDestinationFilePath());
            preprocessingState.saveBuffersToFile(outFile);
        }
        return preprocessingState;
    }

    private static String processStringForTailRemover(final String str) {
        final int tailRemoverStart = str.indexOf("/*-*/");
        if (tailRemoverStart >= 0) {
            return str.substring(0, tailRemoverStart);
        }
        return str;
    }

    private boolean checkDirectiveArgumentRoughly(final AbstractDirectiveHandler directive, final String rest) {
        final DirectiveArgumentType argument = directive.getArgumentType();
        
        boolean result;
        final String trimmedRest = rest.trim();
        
        switch(argument){
            case NONE : {
                result = trimmedRest.isEmpty();
            }break;
            case ONOFF : {
                if (trimmedRest.isEmpty()) {
                    result = false;
                } else {
                    final char firstChar = rest.charAt(0);
                    result = firstChar == '+' || firstChar == '-';
                    if (rest.length()>1){
                        result = result && Character.isSpaceChar(rest.charAt(1));
                    } 
                }
            }break;
            default:
            {
                result = !trimmedRest.isEmpty() && Character.isSpaceChar(rest.charAt(0));
            }break;
        }
        
        return result;
    }
    
    protected AfterProcessingBehaviour processDirective(final PreprocessingState state, final String trimmedString, final PreprocessorContext configurator, final boolean firstPass) throws IOException {
        final boolean executionEnabled = state.isDirectiveCanBeProcessed();

        for (final AbstractDirectiveHandler handler : AbstractDirectiveHandler.DIRECTIVES) {
            final String name = handler.getName();
            if (trimmedString.startsWith(name)) {
                if ((firstPass && !handler.isGlobalPhaseAllowed()) || (!firstPass && !handler.isPreprocessingPhaseAllowed())) {
                    return AfterProcessingBehaviour.READ_NEXT_LINE;
                }
                
                final boolean allowedForExecution = executionEnabled || !handler.executeOnlyWhenExecutionAllowed();

                final String restOfString = PreprocessorUtils.extractTail(name, trimmedString);
                if (checkDirectiveArgumentRoughly(handler, restOfString)) {
                    if (allowedForExecution) {
                        return handler.execute(restOfString.trim(), state, configurator);
                    } else {
                        return AfterProcessingBehaviour.PROCESSED;
                    }
                } else {
                    throw new RuntimeException("Directive " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + handler.getName() + " has wrong argument");
                }
            }
        }
        throw new RuntimeException("Unknown preprocessor directive [" + trimmedString + ']');
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
