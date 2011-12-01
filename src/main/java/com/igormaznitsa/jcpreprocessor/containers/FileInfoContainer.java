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
package com.igormaznitsa.jcpreprocessor.containers;

import com.igormaznitsa.jcpreprocessor.context.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.directives.AbstractDirectiveHandler;
import com.igormaznitsa.jcpreprocessor.directives.AfterDirectiveProcessingBehaviour;
import com.igormaznitsa.jcpreprocessor.directives.DirectiveArgumentType;
import com.igormaznitsa.jcpreprocessor.exceptions.FilePositionInfo;
import com.igormaznitsa.jcpreprocessor.exceptions.PreprocessorException;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The class is one from the main classes in the preprocessor because it describes a preprocessing file and contains business logic for the process
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class FileInfoContainer {
    /**
     * The source file for the container
     */
    private final File sourceFile;
    
    /**
     * The flag shows that the file should be just copied into the destination place without any preprocessing
     */
    private final boolean forCopyOnly;
    
    /**
     * The flag shows that the file has been excluded from preprocessing and it will not be preprocessed and copied
     */
    private boolean excludedFromPreprocessing;
    
    /**
     * The destination directory for the file
     */
    private String destinationDir;
    
    /**
     * The destination name for the file
     */
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

    public FileInfoContainer(final File srcFile, final String dstFileName, final boolean copyOnly) {
        if (srcFile == null){
            throw new NullPointerException("The source file is null");
        }
        
        if (dstFileName == null){
            throw new NullPointerException("The destination file name is null");
        }
        
        forCopyOnly = copyOnly;
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
        return "FileInfoContainer: file="+PreprocessorUtils.getFilePath(sourceFile)+" toDir="+destinationDir+" toName="+destinationName;
    }

    public List<PreprocessingState.ExcludeIfInfo> processGlobalDirectives(final PreprocessingState state, final PreprocessorContext context) throws PreprocessorException, IOException {
        final PreprocessingState preprocessingState = state == null ? context.produceNewPreprocessingState(this) : state;

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
                    switch (processDirective(preprocessingState, PreprocessorUtils.extractTail(AbstractDirectiveHandler.DIRECTIVE_PREFIX, trimmedProcessingString), context, true)) {
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
        final PreprocessingState preprocessingState = state != null ? state : context.produceNewPreprocessingState(this);

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

                String stringPrefix = "";
                if (numberOfSpacesAtTheLineBeginning>0){
                    stringPrefix = nonTrimmedProcessingString.substring(0, numberOfSpacesAtTheLineBeginning);
                }
                
                String stringToBeProcessed = trimmedProcessingString;

                if (stringToBeProcessed.startsWith(AbstractDirectiveHandler.DIRECTIVE_PREFIX)) {
                    switch (processDirective(preprocessingState, PreprocessorUtils.extractTail(AbstractDirectiveHandler.DIRECTIVE_PREFIX, stringToBeProcessed), context, false)) {
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
                        stringToBeProcessed = PreprocessorUtils.processMacroses(trimmedProcessingString, context);
                    }

                    if (startsWithTwoDollars) {
                        // Output the tail of the string to the output stream without comments and macroses
                        preprocessingState.getPrinter().print(stringPrefix);
                        preprocessingState.getPrinter().println(PreprocessorUtils.extractTail("//$$", trimmedProcessingString));
                    } else if (stringToBeProcessed.startsWith("//$")) {
                        // Output the tail of the string to the output stream without comments
                        preprocessingState.getPrinter().print(stringPrefix);
                        preprocessingState.getPrinter().println(PreprocessorUtils.extractTail("//$", stringToBeProcessed));
                    } else {
                        // Just string
                        final String strToOut = processStringForTailRemover(stringToBeProcessed);

                        if (preprocessingState.getPreprocessingFlags().contains(PreprocessingFlag.COMMENT_NEXT_LINE)) {
                            preprocessingState.getPrinter().print("//");
                            preprocessingState.getPreprocessingFlags().remove(PreprocessingFlag.COMMENT_NEXT_LINE);
                        }

                        preprocessingState.getPrinter().print(stringPrefix);
                        preprocessingState.getPrinter().println(strToOut);
                    }
                }

            }
        } catch (Exception unexpected) {
            final String message = unexpected.getMessage() == null ? "Unexpected exception" : unexpected.getMessage();
            throw preprocessingState.makeException(message, trimmedProcessingString, unexpected);
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

        if (!context.isFileOutputDisabled()) {
            final File outFile = context.makeDestinationFile(getDestinationFilePath());
            preprocessingState.saveBuffersToFile(outFile, context.isRemoveComments());
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

        switch (argument) {
            case NONE: {
                result = trimmedRest.isEmpty();
            }
            break;
            case ONOFF: {
                if (trimmedRest.isEmpty()) {
                    result = false;
                } else {
                    final char firstChar = rest.charAt(0);
                    result = firstChar == '+' || firstChar == '-';
                    if (rest.length() > 1) {
                        result = result && Character.isSpaceChar(rest.charAt(1));
                    }
                }
            }
            break;
            default: {
                result = !trimmedRest.isEmpty() && Character.isSpaceChar(rest.charAt(0));
            }
            break;
        }

        return result;
    }

    protected AfterDirectiveProcessingBehaviour processDirective(final PreprocessingState state, final String trimmedString, final PreprocessorContext configurator, final boolean firstPass) throws IOException {
        final boolean executionEnabled = state.isDirectiveCanBeProcessed();

        for (final AbstractDirectiveHandler handler : AbstractDirectiveHandler.DIRECTIVES) {
            final String name = handler.getName();
            if (trimmedString.startsWith(name)) {
                if ((firstPass && !handler.isGlobalPhaseAllowed()) || (!firstPass && !handler.isPreprocessingPhaseAllowed())) {
                    return AfterDirectiveProcessingBehaviour.READ_NEXT_LINE;
                }

                final boolean allowedForExecution = executionEnabled || !handler.executeOnlyWhenExecutionAllowed();

                final String restOfString = PreprocessorUtils.extractTail(name, trimmedString);
                if (checkDirectiveArgumentRoughly(handler, restOfString)) {
                    if (allowedForExecution) {
                        return handler.execute(restOfString.trim(), configurator);
                    } else {
                        return AfterDirectiveProcessingBehaviour.PROCESSED;
                    }
                } else {
                    throw new IllegalArgumentException("Directive " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + handler.getName() + " has wrong argument");
                }
            }
        }
        throw new IllegalArgumentException("Unknown preprocessor directive detected [" + trimmedString + ']');
    }

    public void setDestinationDir(final String destDir) {
        if (destDir == null){
            throw new NullPointerException("String is null");
        }
        destinationDir = destDir;
    }

    public void setDestinationName(final String destName) {
        if (destName == null) {
            throw new NullPointerException("String is null");
        }
        destinationName = destName;
    }

    public void setExcluded(final boolean flag) {
        excludedFromPreprocessing = flag;
    }
}
