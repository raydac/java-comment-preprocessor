package com.igormaznitsa.jcpreprocessor.exceptions;

import java.io.File;

public class PreprocessorException extends Exception {
    private static final long serialVersionUID = 2857499664112391862L;
    private final String processingString;

    private final FilePositionInfo [] callStack;
    
    public PreprocessorException(final String message, final String processinText, final FilePositionInfo [] callStack, final Throwable cause) {
        super(message, cause);
        
        this.processingString = processinText;
        this.callStack = callStack;
    }

    public File getRootFile() {
        if (callStack.length==0)
            return null;
        else
            return callStack[callStack.length-1].getFile();
    }

    public File getProcessingFile() {
        if (callStack.length==0)
            return null;
        else 
        return callStack[0].getFile();
    }

    public int getStringIndex() {
        if (callStack.length ==0){
            return -1;
        } else {
            return callStack[0].getStringIndex()+1;
        }
    }

    public String getProcessingString() {
        return processingString;
    }

    private static String fieldText(final String name, final String text) {
        return '[' + name + '=' + text + ']';
    }

    private String makeCallStackAsString(){
        final StringBuilder result = new StringBuilder();
        for(int i=0;i<callStack.length;i++){
            if (i>0){
                result.append("<-");
            }
            result.append(callStack[i].toString());
        }
        return result.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        result.append(getMessage());
        result.append(processingString);
        result.append('[').append(makeCallStackAsString()).append(']');

        return result.toString();
    }
}
