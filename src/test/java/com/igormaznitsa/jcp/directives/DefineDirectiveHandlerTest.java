package com.igormaznitsa.jcp.directives;

import org.junit.Test;
import static org.junit.Assert.*;

public class DefineDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {

    private static final DefineDirectiveHandler HANDLER = new DefineDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_define.txt", null, null);
    }

    @Test
    public void testExecution_wrongCases() {
        assertPreprocessorException("\n\n//#define \n", 3, null);
        assertPreprocessorException("\n\n//#define 1223\n", 3, null);
        assertPreprocessorException("\n\n//#define \"test\"\n", 3, null);
    }
    
    @Override
    public void testKeyword() throws Exception {
        assertEquals("define", HANDLER.getName());
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
        assertEquals(DirectiveArgumentType.VARNAME, HANDLER.getArgumentType());
    }
    
}
