package com.igormaznitsa.jcp.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import static org.junit.Assert.*;
import org.junit.Test;

public class PreprocessorUtilsTest {
    
    @Test
    public void testMakeFileReader_charsetAndBufferSizeChange() throws Exception {
        final Charset defaultCharset = Charset.defaultCharset();
        final File testFile = new File(PreprocessorUtilsTest.class.getResource("somefile.txt").toURI());
        
        Charset nonDefaultCharset = null;
        for(final Charset ch : Charset.availableCharsets().values()){
            if (!defaultCharset.equals(ch)){
                nonDefaultCharset = ch;
                break;
            }
        }
        
        assertNotNull("We must have found a non default charset, system must have more than one available charset",nonDefaultCharset);
        
        // some hack to get access to the wrapped reader
        final Field inField = BufferedReader.class.getDeclaredField("in");
        inField.setAccessible(true);
        final Field cbField = BufferedReader.class.getDeclaredField("cb");
        cbField.setAccessible(true);
        
        final int BUFFER_SIZE = 0xCAFE;
        
        final BufferedReader reader = PreprocessorUtils.makeFileReader(testFile, nonDefaultCharset.name(), BUFFER_SIZE);
        
        // check that we have selected the non standard charset
        final InputStreamReader wrappedReader = (InputStreamReader) inField.get(reader);
        assertNotNull("Must not be null",wrappedReader);
        assertEquals("The non default charset must be set", nonDefaultCharset.name(), wrappedReader.getEncoding());
    
        // check that we have changed the buffer size
        final char [] insideCharBuffer = (char[]) cbField.get(reader);
        assertNotNull("Must not be null", insideCharBuffer);
        assertEquals("Must have our selected size", BUFFER_SIZE, insideCharBuffer.length);
        
    }
}
