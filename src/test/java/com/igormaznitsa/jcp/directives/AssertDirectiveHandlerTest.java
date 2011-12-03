package com.igormaznitsa.jcp.directives;

import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import org.mockito.Mockito;
import static org.junit.Assert.*;

public class AssertDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {

    private static final AssertDirectiveHandler HANDLER = new AssertDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        final PreprocessorLogger mock = Mockito.mock(PreprocessorLogger.class);
        assertFilePreprocessing("directive_assert.txt", null, mock);
        Mockito.verify(mock).info("string 2 ok");
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("assert", HANDLER.getName());
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
        assertEquals(DirectiveArgumentType.TAIL, HANDLER.getArgumentType());
    }

    @Override
    public void testPhase() throws Exception {
        assertTrue(HANDLER.isPreprocessingPhaseAllowed());
        assertFalse(HANDLER.isGlobalPhaseAllowed());
    }
}
