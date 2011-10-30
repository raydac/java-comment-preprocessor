package com.igormaznitsa.jcpreprocessor.directives;

import static org.junit.Assert.*;

public class OutDisabledDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {
    
    private static final OutDisabledDirectiveHandler HANDLER = new OutDisabledDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_outdisabled.txt", null);
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("-", HANDLER.getName());
    }

    @Override
    public void testHasExpression() throws Exception {
        assertFalse(HANDLER.hasExpression());
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
        assertTrue(HANDLER.isPreprocessingPhaseAllowed());
        assertFalse(HANDLER.isGlobalPhaseAllowed());
    }
}
