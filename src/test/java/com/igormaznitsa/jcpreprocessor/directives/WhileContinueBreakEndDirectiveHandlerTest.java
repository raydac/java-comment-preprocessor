package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.Test;
import static org.junit.Assert.*;

public class WhileContinueBreakEndDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {

    private static final WhileDirectiveHandler WHILE_HANDLER = new WhileDirectiveHandler();
    private static final ContinueDirectiveHandler CONTINUE_HANDLER = new ContinueDirectiveHandler();
    private static final BreakDirectiveHandler BREAK_HANDLER = new BreakDirectiveHandler();
    private static final EndDirectiveHandler END_HANDLER = new EndDirectiveHandler();
    
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
        assertEquals("while",WHILE_HANDLER.getName());
        assertEquals("break",BREAK_HANDLER.getName());
        assertEquals("continue",CONTINUE_HANDLER.getName());
        assertEquals("end",END_HANDLER.getName());
    }

    @Override
    public void testHasExpression() throws Exception {
        assertTrue(WHILE_HANDLER.hasExpression());
        assertFalse(BREAK_HANDLER.hasExpression());
        assertFalse(CONTINUE_HANDLER.hasExpression());
        assertFalse(END_HANDLER.hasExpression());
    }

    @Override
    public void testExecutionCondition() throws Exception {
        assertFalse(WHILE_HANDLER.executeOnlyWhenExecutionAllowed());
        assertTrue(BREAK_HANDLER.executeOnlyWhenExecutionAllowed());
        assertTrue(CONTINUE_HANDLER.executeOnlyWhenExecutionAllowed());
        assertFalse(END_HANDLER.executeOnlyWhenExecutionAllowed());
    }

    @Override
    public void testReference() throws Exception {
        assertReference(WHILE_HANDLER);
        assertReference(BREAK_HANDLER);
        assertReference(CONTINUE_HANDLER);
        assertReference(END_HANDLER);
    }

    @Override
    public void testPhase() throws Exception {
        assertFalse(WHILE_HANDLER.isGlobalPhaseAllowed());
        assertFalse(BREAK_HANDLER.isGlobalPhaseAllowed());
        assertFalse(CONTINUE_HANDLER.isGlobalPhaseAllowed());
        assertFalse(END_HANDLER.isGlobalPhaseAllowed());

        assertTrue(WHILE_HANDLER.isPreprocessingPhaseAllowed());
        assertTrue(BREAK_HANDLER.isPreprocessingPhaseAllowed());
        assertTrue(CONTINUE_HANDLER.isPreprocessingPhaseAllowed());
        assertTrue(END_HANDLER.isPreprocessingPhaseAllowed());
    }
}
