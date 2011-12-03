package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class VerboseHandlerTest extends AbstractCommandLineHandlerTest {

    private static final VerboseHandler HANDLER = new VerboseHandler();
    
    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = mock(PreprocessorContext.class);
        
        assertFalse(HANDLER.processCommandLineKey(null, mock));
        assertFalse(HANDLER.processCommandLineKey("", mock));
        assertFalse(HANDLER.processCommandLineKey("/v:", mock));
        assertFalse(HANDLER.processCommandLineKey("/VV", mock));
        
        assertTrue(HANDLER.processCommandLineKey("/v", mock));
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
