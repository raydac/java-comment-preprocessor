package com.igormaznitsa.jcpreprocessor.directives;

import static org.junit.Assert.*;

public class CommentNextLineDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest{

    private static final CommentNextLineDirectiveHandler TEST_HANDLER = new CommentNextLineDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_comment_next_line.txt", null);
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("//", TEST_HANDLER.getName());
    }

    @Override
    public void testHasExpression() throws Exception {
        assertFalse(TEST_HANDLER.hasExpression());
    }

    @Override
    public void testExecutionCondition() throws Exception {
        assertTrue(TEST_HANDLER.executeOnlyWhenExecutionAllowed());
    }

    @Override
    public void testReference() throws Exception {
        assertReference(TEST_HANDLER);
    }

    @Override
    public void testPhase() throws Exception {
        assertFalse(TEST_HANDLER.isGlobalPhaseAllowed());
        assertTrue(TEST_HANDLER.isPreprocessingPhaseAllowed());
    }
    
}
