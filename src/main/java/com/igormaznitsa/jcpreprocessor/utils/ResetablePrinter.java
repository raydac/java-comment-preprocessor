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
