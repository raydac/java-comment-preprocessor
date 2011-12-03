package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GlobalVariableHandlerTest extends AbstractCommandLineHandlerTest {

    private static final GlobalVariableHandler HANDLER = new GlobalVariableHandler();
    
    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = mock(PreprocessorContext.class);
        
        assertFalse(HANDLER.processCommandLineKey(null, mock));
        assertFalse(HANDLER.processCommandLineKey("", mock));
        assertFalse(HANDLER.processCommandLineKey("/p", mock));
        assertFalse(HANDLER.processCommandLineKey("/pp:", mock));
        assertFalse(HANDLER.processCommandLineKey("/P:", mock));
        
        assertTrue(HANDLER.processCommandLineKey("/P:hello=0", mock));
    
        verify(mock).setGlobalVariable("hello", Value.INT_ZERO);
    }

    @Override
    public void testName() {
        assertEquals("/P:",HANDLER.getKeyName());
    }

    @Override
    public void testDescription() {
        assertDescription(HANDLER);
    }
    
}
