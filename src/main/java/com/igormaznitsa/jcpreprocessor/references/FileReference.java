package com.igormaznitsa.jcpreprocessor.references;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.directives.AbstractDirectiveHandler;
import com.igormaznitsa.jcpreprocessor.directives.DirectiveBehaviourEnum;
import com.igormaznitsa.jcpreprocessor.directives.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.removers.JavaCommentsRemover;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class FileReference {

    private final File sourceFile;
    private final boolean onlyForCopy;
    private boolean excluded;
    private String destinationDir;
    private String destinationName;

    public File getSourceFile() {
        return sourceFile;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public boolean isOnlyForCopy() {
        return onlyForCopy;
    }

    public String getDestinationDir() {
        return destinationDir;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public FileReference(final File srcFile, final String dstFileName, final boolean copingOnly) {
        onlyForCopy = copingOnly;
        excluded = false;
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

    public void preprocess(final PreprocessorContext configurator) throws IOException {
        configurator.clearLocalVariables();

        final ParameterContainer paramContainer = new ParameterContainer(this, getSourceFile(), configurator.getCharacterEncoding());
        paramContainer.setStrings(PreprocessorUtils.readTextFileAndAddNullAtEnd(getSourceFile(), configurator.getCharacterEncoding()));

        paramContainer.pushFileName(paramContainer.getCurrentFileCanonicalPath());

        preprocess(paramContainer, configurator);

        File p_outfile = configurator.makeDestinationFile(getDestinationFilePath());

        if (paramContainer.saveBuffersToFile(p_outfile)) {
            if (configurator.isRemovingComments()) {
                removeCommentsFromFile(p_outfile, configurator);
            }
        }
    }

    public void preprocess(final ParameterContainer paramContainer, final PreprocessorContext configurator) throws IOException {
         try {
            while (true) {
                String nonTrimmedProcessingString = paramContainer.nextLine();
                if (paramContainer.shouldEndPreprocessing()) {
                    nonTrimmedProcessingString = null;
                }

                if (nonTrimmedProcessingString == null) {
                    if (!paramContainer.isIncludeReferenceEmpty()) {
                        IncludeReference p_inRef = paramContainer.popIncludeReference();
                        paramContainer.setCurrentProcessingFile(p_inRef.getFile());
                        paramContainer.setStrings(p_inRef.getStrings()).setCurrentStringIndex(p_inRef.getStringCounter()).setEndPreprocessing(false);
                        paramContainer.popFileName();
                        paramContainer.setCurrentFileCanonicalPath("some strange");
                        continue;
                    } else {
                        break;
                    }
                }

                final String trimmedProcessingString = nonTrimmedProcessingString.trim();

                final int numberOfSpacesAtTheLineBeginning = nonTrimmedProcessingString.indexOf(trimmedProcessingString);

                String stringToBeProcessed;

                if (paramContainer.isProcessingEnabled()) {
                    stringToBeProcessed = PreprocessorUtils.processMacros(getSourceFile(), trimmedProcessingString, configurator);
                } else {
                    stringToBeProcessed = trimmedProcessingString;
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
                        if (stringToBeProcessed.startsWith("//$$") && paramContainer.isProcessingEnabled() && paramContainer.isOutEnabled()) {
                            // Output the tail of the string to the output stream without comments and macroses
                            stringToBeProcessed = stringToBeProcessed.substring(4);
                            paramContainer.println(stringToBeProcessed);
                        } else if (stringToBeProcessed.startsWith("//$") && paramContainer.isProcessingEnabled() && paramContainer.isOutEnabled()) {
                            // Output the tail of the string to the output stream without comments
                            stringToBeProcessed = stringToBeProcessed.substring(3);
                            paramContainer.println(stringToBeProcessed);
                        } else if (paramContainer.isProcessingEnabled()) {
                            // Just string :)
                            if (paramContainer.isOutEnabled()) {
                                if (!stringToBeProcessed.startsWith("//#")) {
                                    int i_indx;

                                    i_indx = stringToBeProcessed.indexOf("/*-*/");
                                    if (i_indx >= 0) {
                                        stringToBeProcessed = stringToBeProcessed.substring(0, i_indx);
                                    }

                                    if (paramContainer.shouldCommentNextLine()) {
                                        paramContainer.print("// ");
                                        paramContainer.setCommentNextLine(false);
                                    }
                                    for (int li = 0; li < numberOfSpacesAtTheLineBeginning; li++) {
                                        paramContainer.print(" ");
                                    }
                                    paramContainer.println(stringToBeProcessed);
                                } else {
                                    throw new IOException("An unsupported preprocessor directive has been found " + stringToBeProcessed);
                                }
                            }
                        }
                    }
                    break;
                }


            }
        } catch (IOException e) {
            throw new IOException(e.getMessage() + " file: " + paramContainer.getCurrentFileCanonicalPath() + " str: " + paramContainer.getCurrentStringIndex());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage() + " file: " + paramContainer.getCurrentFileCanonicalPath() + " str: " + paramContainer.getCurrentStringIndex(), ex);
        }

        if (!paramContainer.isIfCounterZero()) {
            throw new IOException("You have an unclosed #if construction [" + paramContainer.getCurrentFileCanonicalPath() + ':' + paramContainer.getLastIfStringNumber() + ']');
        }
        if (!paramContainer.isWhileCounterZero()) {
            throw new IOException("You have an unclosed #while construction [" + paramContainer.getCurrentFileCanonicalPath() + ':' + paramContainer.getLastWhileStringNumber() + ']');
        }

       
    }
    
    protected DirectiveBehaviourEnum processDirective(final ParameterContainer state, final String string, final PreprocessorContext configurator) throws IOException {
        if (string.startsWith("//#")) {
            final String tail = PreprocessorUtils.extractTail("//#", string);
            for (final AbstractDirectiveHandler handler : AbstractDirectiveHandler.DIRECTIVES) {
                final String name = handler.getName();
                if (tail.startsWith(name)) {
                    final String s = PreprocessorUtils.extractTail(name, tail);
                    if (handler.hasExpression()) {
                        if (!s.isEmpty() && Character.isSpaceChar(s.charAt(0))) {
                            return handler.execute(s.trim(), state, configurator);
                        } else {
                            continue;
                        }
                    } else {
                        return handler.execute(s.trim(), state, configurator);
                    }
                }
            }
            throw new RuntimeException("Unknown cmd [" + string + ']');
        }

        return DirectiveBehaviourEnum.NOT_PROCESSED;
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
        excluded = flag;
    }
}
