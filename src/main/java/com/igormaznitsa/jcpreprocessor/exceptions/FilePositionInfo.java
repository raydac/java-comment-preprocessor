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
