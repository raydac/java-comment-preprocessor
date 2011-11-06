package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class VerboseHandlerTest extends AbstractCommandLineHandlerTest {

    private static final VerboseHandler HANDLER = new VerboseHandler();
    
    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = mock(PreprocessorContext.class);
        
        assertFalse(HANDLER.processArgument(null, mock));
        assertFalse(HANDLER.processArgument("", mock));
        assertFalse(HANDLER.processArgument("/v:", mock));
        assertFalse(HANDLER.processArgument("/VV", mock));
        
        assertTrue(HANDLER.processArgument("/v", mock));
        verify(mock).setVerbose(true);
    }

    @Override
    public void testName() {
        assertEquals("/V", HANDLER.getKeyName());
    }

    @Override
    public void testDescription() {
        assertDescription(HANDLER);
    }
    
}
