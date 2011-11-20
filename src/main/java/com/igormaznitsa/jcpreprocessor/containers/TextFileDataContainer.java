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

import java.io.File;

/**
 * The class contains text data of a file and the string position index for the file 
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class TextFileDataContainer {

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
            return file.equals(thatItem.file) && nextStringIndex == thatItem.nextStringIndex;
        }
        return false;
    }
}
