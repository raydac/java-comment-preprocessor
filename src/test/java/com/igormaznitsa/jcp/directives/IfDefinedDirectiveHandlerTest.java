package com.igormaznitsa.jcp.directives;

import static org.junit.Assert.*;

public class IfDefinedDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {

    private static final IfDefinedDirectiveHandler HANDLER = new IfDefinedDirectiveHandler();

    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_ifdefined.txt", false, null, null, new VariablePair("BYTECODE", "true"));
        
        try{
          assertFilePreprocessing("directive_ifdefined.txt", false, null, null);
        }catch(LinesNotMatchException expected){
          assertEquals("somebytecode", expected.getEtalonString());
          assertEquals("end",expected.getResultString());
        }
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("ifdefined", HANDLER.getName());
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

    @Override
    public void testArgumentType() throws Exception {
        assertEquals(DirectiveArgumentType.VARNAME, HANDLER.getArgumentType());
    }
}
