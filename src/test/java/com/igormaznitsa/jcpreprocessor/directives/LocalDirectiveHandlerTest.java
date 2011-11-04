package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.Test;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.junit.Assert.*;

public class LocalDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {

    private static final LocalDirectiveHandler HANDLER = new LocalDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext context = assertFilePreprocessing("directive_local.txt", null);

        assertEquals(Long.valueOf(5), context.getLocalVariable("x").asLong());
        assertEquals(Long.valueOf(10), context.getLocalVariable("y").asLong());
        assertEquals(Long.valueOf(15), context.getLocalVariable("z").asLong());
    }

    @Override
    public void testExecutionCondition() throws Exception {
        assertTrue(HANDLER.executeOnlyWhenExecutionAllowed());
    }

    @Test
    public void testExecution_ExceptionOnExpressionAbsence() {
        assertPreprocessorException("1\n2\n   //#local \n3", 3, null);
        assertPreprocessorException("1\n2\n   //#local\n3", 3, null);
    }

    @Test
    public void testExecution_ExceptionOnWrongExpression() {
        assertPreprocessorException("1\n2\n   //#local 3\n3", 3, null);
        assertPreprocessorException("1\n2\n   //#local a=\n3", 3, null);
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("local", HANDLER.getName());
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
        assertEquals(DirectiveArgumentType.SET, HANDLER.getArgumentType());
    }
}
