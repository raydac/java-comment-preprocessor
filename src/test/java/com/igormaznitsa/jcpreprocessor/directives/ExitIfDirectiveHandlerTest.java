package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExitIfDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest{
    private static final ExitIfDirectiveHandler HANDLER = new ExitIfDirectiveHandler();

    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_exitif.txt", null);
    }
    
    @Test
    public void testExecution_wrongExpression() {
        assertPreprocessorException("\n\n //#exitif \"test\"", 3);
        assertPreprocessorException("\n\n //#exitif ", 3);
        assertPreprocessorException("\n\n //#exitif 111", 3);
        assertPreprocessorException("\n\n //#exitif", 3);
    }
    
    @Override
    public void testKeyword() throws Exception {
        assertEquals("exitif", HANDLER.getName());
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
        assertFalse(HANDLER.isGlobalPhaseAllowed());
        assertTrue(HANDLER.isPreprocessingPhaseAllowed());
    }
    
}