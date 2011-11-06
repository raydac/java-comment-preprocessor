package com.igormaznitsa.jcpreprocessor.exceptions;

import java.io.File;

public class FilePositionInfo {
    private final File file;
    private final int stringIndex;
    
    public FilePositionInfo(final File file, final int stringIndex){
        if (file == null) {
            throw new NullPointerException("File is null");
        }
        this.file = file;
        this.stringIndex = stringIndex;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public int getStringIndex() {
        return this.stringIndex;
    }
    
    public String toString() {
        final String filePath = this.file.getAbsolutePath();
        return filePath+':'+Integer.toString(stringIndex);
    }
}
