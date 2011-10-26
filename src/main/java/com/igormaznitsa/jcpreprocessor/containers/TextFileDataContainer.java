package com.igormaznitsa.jcpreprocessor.containers;

import java.io.File;

public class TextFileDataContainer {

    private final String[] text;
    private final File file;
    
    private int nextStringIndex;
    
    public String[] getText() {
        return text.clone();
    }

    public File getFile() {
        return file;
    }

    public void reset() {
        nextStringIndex = 0;
    }

    public String nextLine() {
        if (nextStringIndex >= text.length) {
            return null;
        } else {
            return text[nextStringIndex++];
        }
    }

    public void setNextStringIndex(final int index){
        if (index<0 || index>=text.length){
            throw new IndexOutOfBoundsException("String index out of bound ["+index+']');
        }
        this.nextStringIndex = index; 
    }
    
    public int getNextStringIndex() {
        return nextStringIndex;
    }

    public TextFileDataContainer(final TextFileDataContainer item, final int nextStringIndex){
        this(item.file,item.text,nextStringIndex);
    }
    
    public TextFileDataContainer(final File currentFile, final String[] text, final int nextStringIndex) {
        if (currentFile == null) {
            throw new NullPointerException("File is null");
        }
        
        if (text == null) {
            throw new NullPointerException("Text is null");
        }
        
        this.file = currentFile;
        this.text = text;
        setNextStringIndex(nextStringIndex);
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public boolean equals(final Object that) {
        if (that == null) {
            return false;
        }

        if (this == that) {
            return true;
        }

        if (that instanceof TextFileDataContainer) {
            final TextFileDataContainer thatItem = (TextFileDataContainer) that;
            return file.equals(that) && nextStringIndex == thatItem.nextStringIndex;
        }
        return false;
    }
}
