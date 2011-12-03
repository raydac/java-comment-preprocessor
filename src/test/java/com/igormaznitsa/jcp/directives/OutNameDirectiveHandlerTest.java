package com.igormaznitsa.jcp.directives;

import org.junit.Test;
import static org.junit.Assert.*;

public class OutNameDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {
    
    private static final OutNameDirectiveHandler HANDLER = new OutNameDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_outname.txt", null, null);
    }

    @Test
    public void testExecution_wrongExpressionResult() {
        assertPreprocessorException("\n//#outname", 2, null);
        assertPreprocessorException("\n//#outname 882772", 2, null);
    }
    
    @Override
    public void testKeyword() throws Exception {
        assertEquals("outname", HANDLER.getName());
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
        assertEquals(DirectiveArgumentType.STRING, HANDLER.getArgumentType());
    }
}
