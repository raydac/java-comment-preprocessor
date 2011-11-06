package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.Test;
import static org.junit.Assert.*;

public class FlushDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {

    private static final FlushDirectiveHandler HANDLER = new FlushDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        //TODO make execution test
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("flush",HANDLER.getName());
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
    public void testArgumentType() throws Exception {
        assertEquals(DirectiveArgumentType.NONE, HANDLER.getArgumentType());
    }

    @Override
    public void testPhase() throws Exception {
        assertFalse(HANDLER.isGlobalPhaseAllowed());
        assertTrue(HANDLER.isPreprocessingPhaseAllowed());
    }
    
}
