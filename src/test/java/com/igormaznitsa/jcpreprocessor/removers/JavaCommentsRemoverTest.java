package com.igormaznitsa.jcpreprocessor.removers;

import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Test;
import static org.junit.Assert.*;

public class JavaCommentsRemoverTest {
    
    @Test
    public void testRemovingSingleStringComments() throws Exception {
        final String SRC = "class main() {\n// hello world\nSystem.out.println(\"hello // world\");// a comment\n}";
        final String DST = "class main() {\n\nSystem.out.println(\"hello // world\");\n}";
        
        final StringReader reader = new StringReader(SRC);
        final StringWriter writer = new StringWriter(256);
        
        new JavaCommentsRemover(reader, writer).process();
        
        assertEquals("Must be the same", DST, writer.toString());
    }

    @Test
    public void testMultilineStringComments() throws Exception {
        final String SRC = "class main() {/**\ntest\n*/\n\n// hello world\nSystem.out.println(\"hello /*ooo*/ world\");/* a comment*/\n/*  aslajdhkajhdkqwiueyoqiweuoqwueoqwiue}";
        final String DST = "class main() {\n\n\nSystem.out.println(\"hello /*ooo*/ world\");\n";
        
        final StringReader reader = new StringReader(SRC);
        final StringWriter writer = new StringWriter(256);
        
        new JavaCommentsRemover(reader, writer).process();
        
        assertEquals("Must be the same", DST, writer.toString());
    }
}
