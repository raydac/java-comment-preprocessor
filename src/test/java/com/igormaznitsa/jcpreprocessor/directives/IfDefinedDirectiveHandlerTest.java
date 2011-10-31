package com.igormaznitsa.jcpreprocessor.directives;

import static org.junit.Assert.*;

public class IfDefinedDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {

    private static final IfDefinedDirectiveHandler HANDLER = new IfDefinedDirectiveHandler();

    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_ifdefined.txt", null);
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("ifdefined", HANDLER.getName());
    }

    @Override
    public void testHasExpression() throws Exception {
        assertTrue(HANDLER.hasExpression());
    }

    @Override
    public void testExecutionCondition() throws Exception {
        assertFalse(HANDLER.executeOnlyWhenExecutionAllowed());
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
}
