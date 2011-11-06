package com.igormaznitsa.jcpreprocessor.directives;

import static org.junit.Assert.*;

public class ExitDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {
    private static final ExitDirectiveHandler HANDLER = new ExitDirectiveHandler();

    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_exit.txt", null, null);
    }
    
    @Override
    public void testKeyword() throws Exception {
        assertEquals("exit", HANDLER.getName());
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
