package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GlobalVariableHandlerTest extends AbstractCommandLineHandlerTest {

    private static final GlobalVariableHandler HANDLER = new GlobalVariableHandler();
    
    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = mock(PreprocessorContext.class);
        
        assertFalse(HANDLER.processArgument(null, mock));
        assertFalse(HANDLER.processArgument("", mock));
        assertFalse(HANDLER.processArgument("/p", mock));
        assertFalse(HANDLER.processArgument("/pp:", mock));
        assertFalse(HANDLER.processArgument("/P:", mock));
        
        assertTrue(HANDLER.processArgument("/P:hello=0", mock));
    
        verify(mock).setGlobalVariable("hello", Value.INT_ZERO, null);
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
