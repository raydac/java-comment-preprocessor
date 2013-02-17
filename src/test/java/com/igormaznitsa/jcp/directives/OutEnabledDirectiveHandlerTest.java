package com.igormaznitsa.jcp.directives;

import org.junit.Test;
import static org.junit.Assert.*;

public class OutEnabledDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {
    
    private static final OutEnabledDirectiveHandler HANDLER = new OutEnabledDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_outenabled.txt", false, null, null);
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("+", HANDLER.getName());
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
    
    @Override
    public void testArgumentType() throws Exception {
        assertEquals(DirectiveArgumentType.NONE, HANDLER.getArgumentType());
    }
}
