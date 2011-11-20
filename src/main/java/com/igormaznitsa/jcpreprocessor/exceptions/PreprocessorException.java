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
package com.igormaznitsa.jcpreprocessor.exceptions;

import java.io.File;

public class PreprocessorException extends Exception {
    private static final long serialVersionUID = 2857499664112391862L;
    private final String processingString;

    private final FilePositionInfo [] callStack;
    
    public PreprocessorException(final String message, final String processinText, final FilePositionInfo [] callStack, final Throwable cause) {
        super(message, cause);
        
        this.processingString = processinText;
        this.callStack = callStack == null ? new FilePositionInfo[0] : callStack.clone();
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
    
    public FilePositionInfo [] getFileStack(){
        return callStack.clone();
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        result.append(getMessage()).append(' ').append(processingString).
        append(' ').append('[').append(makeCallStackAsString()).append(']');

        return result.toString();
    }
}
