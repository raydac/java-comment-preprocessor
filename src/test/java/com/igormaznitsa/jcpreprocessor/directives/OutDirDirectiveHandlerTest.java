package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.junit.Assert.*;

public class OutDirDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest{

    private static final OutDirDirectiveHandler HANDLER = new OutDirDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext context = assertFilePreprocessing("directive_outdir.txt", null);
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("outdir", HANDLER.getName());
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
        assertTrue(HANDLER.isPreprocessingPhaseAllowed());
        assertFalse(HANDLER.isGlobalPhaseAllowed());
    }
    
}
