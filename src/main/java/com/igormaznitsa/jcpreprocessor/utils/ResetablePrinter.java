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
package com.igormaznitsa.jcpreprocessor.utils;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

public class ResetablePrinter {
    private final CharArrayWriter outStream;
    
    public ResetablePrinter(final int initialCapacity){
        outStream = new CharArrayWriter(initialCapacity);
    }
    
    public boolean isEmpty() {
        return outStream.size() == 0;
    }
    
    public void write(final Writer writer) throws IOException {
        outStream.flush();
        writer.write(outStream.toCharArray());
        writer.flush();
    }
    
    public void reset() {
        outStream.reset();
    }
    
    public void print(final String text) throws IOException{
        for(final char chr : text.toCharArray()){
            outStream.write(chr);
        }
    }
    
    public void println(final String text) throws IOException{
        for(final char chr : text.toCharArray()){
            outStream.write(chr);
        }
        outStream.write('\r');
        outStream.write('\n');
    }
}
