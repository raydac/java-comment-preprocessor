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
package com.igormaznitsa.jcp.exceptions;

import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.File;

/**
 * The class implements a file data storage where an exception can store a snapshot of the current preprocessing file data
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class FilePositionInfo {
    /**
     * The preprocessing file
     */
    private final File file;
    
    /**
     * The current string index in the file
     */
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
    
    @Override
    public String toString() {
        final String filePath = PreprocessorUtils.getFilePath(this.file);
        return filePath+':'+Integer.toString(stringIndex+1);
    }
}
