package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.Test;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.junit.Assert.*;

public class GlobalIfElseEndifTest extends AbstractDirectiveHandlerIntegrationTest {

    private static final GlobalIfDirectiveHandler HANDLER_GLOBAL_IF = new GlobalIfDirectiveHandler();
    private static final GlobalElseDirectiveHandler HANDLER_GLOBAL_ELSE = new GlobalElseDirectiveHandler();
    private static final GlobalEndIfDirectiveHandler HANDLER_GLOBAL_ENDIF = new GlobalEndIfDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext context = executeGlobalPhase("directive_globalifelseendif.txt", null);
        assertTrue(context.containsGlobalVariable("expected"));
        assertFalse(context.containsGlobalVariable("unexpected"));
        assertEquals(Boolean.TRUE, context.findVariableForName("expected", null).asBoolean());
    }

    @Test
    public void testExecution_PreprocessingPhase() throws Exception {
        assertFilePreprocessing("directive_globalifelseendif.txt", null);
        assertFilePreprocessing("directive_globalifelseendif2.txt", null);
    }
    
    @Override
    public void testKeyword() throws Exception {
        assertEquals("_if",HANDLER_GLOBAL_IF.getName());
        assertEquals("_else",HANDLER_GLOBAL_ELSE.getName());
        assertEquals("_endif",HANDLER_GLOBAL_ENDIF.getName());
    }

    @Override
    public void testHasExpression() throws Exception {
        assertTrue(HANDLER_GLOBAL_IF.hasExpression());
        assertFalse(HANDLER_GLOBAL_ELSE.hasExpression());
        assertFalse(HANDLER_GLOBAL_ENDIF.hasExpression());
    }

    @Override
    public void testExecutionCondition() throws Exception {
        assertFalse(HANDLER_GLOBAL_IF.executeOnlyWhenExecutionAllowed());
        assertFalse(HANDLER_GLOBAL_ELSE.executeOnlyWhenExecutionAllowed());
        assertFalse(HANDLER_GLOBAL_ENDIF.executeOnlyWhenExecutionAllowed());
    }

    @Override
    public void testReference() throws Exception {
        assertReference(HANDLER_GLOBAL_IF);
        assertReference(HANDLER_GLOBAL_ELSE);
        assertReference(HANDLER_GLOBAL_ENDIF);
    }

    @Override
    public void testPhase() throws Exception {
        assertTrue(HANDLER_GLOBAL_IF.isGlobalPhaseAllowed());
        assertFalse(HANDLER_GLOBAL_IF.isPreprocessingPhaseAllowed());
        
        assertTrue(HANDLER_GLOBAL_ELSE.isGlobalPhaseAllowed());
        assertFalse(HANDLER_GLOBAL_ELSE.isPreprocessingPhaseAllowed());
        
        assertTrue(HANDLER_GLOBAL_ENDIF.isGlobalPhaseAllowed());
        assertFalse(HANDLER_GLOBAL_ENDIF.isPreprocessingPhaseAllowed());
    }
    
}
