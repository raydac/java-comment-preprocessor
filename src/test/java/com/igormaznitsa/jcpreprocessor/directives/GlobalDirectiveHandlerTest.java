package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.Test;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.junit.Assert.*;

public class GlobalDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {

    private static final GlobalDirectiveHandler HANDLER = new GlobalDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext context = executeGlobalPhase("directive_global.txt",null);
        assertTrue(context.containsGlobalVariable("xxx"));
        final Value var = context.findVariableForName("xxx", null);
        assertEquals(Long.valueOf(10),var.asLong());
    }

    @Test
    public void testExecution_PreprocessingPhase() throws Exception {
        final PreprocessorContext context = assertFilePreprocessing("directive_global.txt",null);
        assertFalse(context.containsGlobalVariable("xxx"));
        assertNull(context.findVariableForName("xxx", null));
    }

    @Test
    public void testExecution_WrongCases() throws Exception {
        assertGlobalPhaseException("\n\n//#global 23123", 3, null);
        assertGlobalPhaseException("\n\n//#global", 3, null);
        assertGlobalPhaseException("\n\n//#global ", 3, null);
        assertGlobalPhaseException("\n\n//#global hh=", 3, null);
        assertGlobalPhaseException("\n\n//#global xx==10", 3, null);
        assertGlobalPhaseException("\n\n//#global =10", 3, null);
        assertGlobalPhaseException("\n\n//#global ====", 3, null);
    }
    
    @Override
    public void testExecutionCondition() throws Exception {
        assertTrue(HANDLER.executeOnlyWhenExecutionAllowed());
    }

    @Override
    public void testHasExpression() throws Exception {
        assertTrue(HANDLER.hasExpression());
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("global", HANDLER.getName());
    }

    @Override
    public void testPhase() throws Exception {
        assertTrue(HANDLER.isGlobalPhaseAllowed());
        assertFalse(HANDLER.isPreprocessingPhaseAllowed());
    }

    @Override
    public void testReference() throws Exception {
        assertReference(HANDLER);
    }

}
