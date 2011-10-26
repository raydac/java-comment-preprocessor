package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.Test;
import com.igormaznitsa.jcpreprocessor.exceptions.PreprocessorException;
import java.util.List;
import java.util.ArrayList;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.junit.Assert.*;

public class IfElseEndifDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {

    @Override
    public void testExecution() throws Exception {
        assertPreprocessing("directive_if_else_endif.txt", null);
    }

    @Test
    public void testExecution_ThereIsNotExpression() throws Exception {
        try {
            final List<String> out = new ArrayList<String>();
            final PreprocessorContext context = preprocessString("\n\n\n   \n  //#if   \ntest\n  //#endif", out, null);
            fail("Must throw PreprocessorException");
        } catch (PreprocessorException expected) {
            assertEquals(5, expected.getStringIndex());
        } catch (Exception unExpected) {
            unExpected.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("if",new IfDirectiveHandler().getName());
        assertEquals("else",new ElseDirectiveHandler().getName());
        assertEquals("endif",new EndIfDirectiveHandler().getName());
    }

    @Override
    public void testHasExpression() throws Exception {
        assertTrue(new IfDirectiveHandler().hasExpression());
        assertFalse(new ElseDirectiveHandler().hasExpression());
        assertFalse(new EndIfDirectiveHandler().hasExpression());
    }

    @Override
    public void testProcessOnlyIfCanBeProcessed() throws Exception {
        assertFalse(new IfDirectiveHandler().processOnlyIfCanBeProcessed());
        assertFalse(new ElseDirectiveHandler().processOnlyIfCanBeProcessed());
        assertFalse(new EndIfDirectiveHandler().processOnlyIfCanBeProcessed());
    }

    @Override
    public void testReference() throws Exception {
        assertReference(new IfDirectiveHandler());
        assertReference(new ElseDirectiveHandler());
        assertReference(new EndIfDirectiveHandler());
    }
    
}
