package com.igormaznitsa.jcp.exceptions;

import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

public class PreprocessorExceptionTest {

    @Test
    public void testExceptionStringIndex_WrongBracket() throws Exception {
        final File file = new File(this.getClass().getResource("wrong_bracket.txt").toURI());

        final PreprocessorContext context = new PreprocessorContext();
        context.setFileOutputDisabled(true);

        final FileInfoContainer container = new FileInfoContainer(file, "test", false);
        try {
            container.preprocessFile(null, context);
            fail("Must throw PreprocessorException");
        } catch (PreprocessorException expected) {
            assertEquals("Must have the right line number", 17, expected.getStringIndex());
        }
    }

    @Test
    public void testExceptionStringIndex_WrongBracketInIncluded() throws Exception {
        final File file = new File(this.getClass().getResource("wrong_bracket_include.txt").toURI());

        final PreprocessorContext context = new PreprocessorContext();
        context.setSourceDirectory(file.getParent());
        context.setFileOutputDisabled(true);

        final FileInfoContainer container = new FileInfoContainer(file, "test", false);
        try {
            container.preprocessFile(null, context);
            fail("Must throw PreprocessorException");
        } catch (PreprocessorException expected) {
            final FilePositionInfo[] fileStack = expected.getFileStack();
            assertEquals("Must have depth 2", 2, fileStack.length);
            assertEquals("String index in the including file is 26", 25, fileStack[1].getStringIndex());
            assertEquals("String index in the wrong bracket file is 15", 16, fileStack[0].getStringIndex());
            
            assertEquals("Must have the right line number", 17, expected.getStringIndex());
        }
    }
}
