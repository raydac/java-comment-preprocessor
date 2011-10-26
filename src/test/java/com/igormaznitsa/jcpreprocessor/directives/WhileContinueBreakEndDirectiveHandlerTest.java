package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.Test;
import static org.junit.Assert.*;

public class WhileContinueBreakEndDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {

    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_while_continue_break_end.txt", null);
    }

    @Test
    public void testWhile_ExceptionWithoutExpression() throws Exception {
        assertPreprocessorException("\n\n\n   \n\n  //#while   \ntest\n  //#end", 6);
    }

    @Test
    public void testWhile_ExceptionForNonBooleanExpression() throws Exception {
        assertPreprocessorException("\n\n\n   \n\n  //#while 234  \ntest\n  //#end", 6);
    }

    @Test
    public void testWhile_ExceptionForNonClosed() throws Exception {
        assertPreprocessorException("\n\n\n   \n\n  //#while true  \ntest\n", 6);
    }

    @Test
    public void testBreak_ExceptionWithoutWhile() throws Exception {
        assertPreprocessorException("\n\n\n   \n\n  //#break \ntest\n", 6);
    }

    @Test
    public void testContinue_ExceptionWithoutWhile() throws Exception {
        assertPreprocessorException("\n\n\n   \n\n  //#continue \ntest\n", 6);
    }

    @Test
    public void testEnd_ExceptionWithoutWhile() throws Exception {
        assertPreprocessorException("\n\n\n   \n\n  //#end \ntest\n", 6);
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
    public void testExecutionCondition() throws Exception {
        assertFalse(new WhileDirectiveHandler().executeOnlyWhenExecutionAllowed());
        assertTrue(new BreakDirectiveHandler().executeOnlyWhenExecutionAllowed());
        assertTrue(new ContinueDirectiveHandler().executeOnlyWhenExecutionAllowed());
        assertFalse(new EndDirectiveHandler().executeOnlyWhenExecutionAllowed());
    }

    @Override
    public void testReference() throws Exception {
        assertReference(new WhileDirectiveHandler());
        assertReference(new BreakDirectiveHandler());
        assertReference(new ContinueDirectiveHandler());
        assertReference(new EndDirectiveHandler());
    }
    
}
