package com.igormaznitsa.jcpreprocessor.exceptions;

import java.io.File;

public class PreprocessorException extends Exception {
    private static final long serialVersionUID = 2857499664112391862L;

    private final File rootProcessingFile;
    private final File processingFile;
    private final String processingString;
    private final int stringIndex;

    public PreprocessorException(final String message, final File rootProcessingFile, final File processingFile, final String processingString, final int processingStringIndex, Throwable cause) {
        super(message, cause);
        this.rootProcessingFile = rootProcessingFile;
        this.processingFile = processingFile;
        this.processingString = processingString;
        this.stringIndex = processingStringIndex;
    }

    public File getRootFile() {
        return rootProcessingFile;
    }

    public File getProcessingFile() {
        return processingFile;
    }

    public int getStringIndex() {
        return stringIndex;
    }

    public String getProcessingString() {
        return processingString;
    }

    private static String getFilePath(final File file) {
        if (file == null) {
            return "null";
        } else {
            return file.getAbsolutePath();
        }
    }

    private static String fieldText(final String name, final String text) {
        return '[' + name + '=' + text + ']';
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        result.append(getMessage());
        result.append(fieldText("root", getFilePath(rootProcessingFile)));
        result.append(fieldText("processing", getFilePath(processingFile)));
        result.append(fieldText("str", Integer.toString(stringIndex)));
        result.append(fieldText("txt", processingString));

        return result.toString();
    }
}
