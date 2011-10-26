package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.Test;
import com.igormaznitsa.jcpreprocessor.exceptions.PreprocessorException;
import java.util.List;
import java.util.ArrayList;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.junit.Assert.*;

public class WhileContinueBreakEndDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {

    @Override
    public void testExecution() throws Exception {
        assertPreprocessing("directive_while_continue_break_end.txt", null);
    }

    @Test
    public void testExecution_WhileThereIsNotExpression() throws Exception {
        try {
            final List<String> out = new ArrayList<String>();
            final PreprocessorContext context = preprocessString("\n\n\n   \n\n  //#while   \ntest\n  //#end", out, null);
            fail("Must throw PreprocessorException");
        } catch (PreprocessorException expected) {
            assertEquals(6, expected.getStringIndex());
        } catch (Exception unExpected) {
            unExpected.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("while",new WhileDirectiveHandler().getName());
        assertEquals("break",new BreakDirectiveHandler().getName());
        assertEquals("continue",new ContinueDirectiveHandler().getName());
        assertEquals("end",new EndDirectiveHandler().getName());
    }

    @Override
    public void testHasExpression() throws Exception {
        assertTrue(new WhileDirectiveHandler().hasExpression());
        assertFalse(new BreakDirectiveHandler().hasExpression());
        assertFalse(new ContinueDirectiveHandler().hasExpression());
        assertFalse(new EndDirectiveHandler().hasExpression());
    }

    @Override
    public void testProcessOnlyIfCanBeProcessed() throws Exception {
        assertFalse(new WhileDirectiveHandler().processOnlyIfCanBeProcessed());
        assertTrue(new BreakDirectiveHandler().processOnlyIfCanBeProcessed());
        assertTrue(new ContinueDirectiveHandler().processOnlyIfCanBeProcessed());
        assertFalse(new EndDirectiveHandler().processOnlyIfCanBeProcessed());
    }

    @Override
    public void testReference() throws Exception {
        assertReference(new WhileDirectiveHandler());
        assertReference(new BreakDirectiveHandler());
        assertReference(new ContinueDirectiveHandler());
        assertReference(new EndDirectiveHandler());
    }
    
}
