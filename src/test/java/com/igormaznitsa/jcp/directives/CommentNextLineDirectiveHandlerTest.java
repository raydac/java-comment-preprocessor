package com.igormaznitsa.jcp.directives;

import static org.junit.Assert.*;

public class CommentNextLineDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest{

    private static final CommentNextLineDirectiveHandler HANDLER = new CommentNextLineDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_comment_next_line.txt", false, null, null);
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("//", HANDLER.getName());
    }

    @Override
    public void testExecutionCondition() throws Exception {
        assertTrue(HANDLER.executeOnlyWhenExecutionAllowed());
    }

    @Override
    public void testReference() throws Exception {
        assertReference(HANDLER);
    }

    @Override
    public void testPhase() throws Exception {
        assertFalse(HANDLER.isGlobalPhaseAllowed());
        assertTrue(HANDLER.isPreprocessingPhaseAllowed());
    }
    
    @Override
    public void testArgumentType() throws Exception {
        assertEquals(DirectiveArgumentType.NONE, HANDLER.getArgumentType());
    }
}
