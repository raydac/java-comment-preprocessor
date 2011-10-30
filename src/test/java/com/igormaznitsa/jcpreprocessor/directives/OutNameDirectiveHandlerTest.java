package com.igormaznitsa.jcpreprocessor.directives;

import static org.junit.Assert.*;

public class OutNameDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {
    
    private static final OutNameDirectiveHandler HANDLER = new OutNameDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_outname.txt", null);
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("outname", HANDLER.getName());
    }

    @Override
    public void testHasExpression() throws Exception {
        assertTrue(HANDLER.hasExpression());
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
