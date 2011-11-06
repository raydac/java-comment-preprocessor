package com.igormaznitsa.jcpreprocessor.cmdline;

import static org.mockito.Mockito.*;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.junit.Assert.*;

public class ClearDstDirectoryHandlerTest extends AbstractCommandLineHandlerTest{

    private static final ClearDstDirectoryHandler HANDLER = new ClearDstDirectoryHandler();

    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = mock(PreprocessorContext.class);
    
        assertFalse(HANDLER.processArgument("/c:", mock));
        assertFalse(HANDLER.processArgument("/CC", mock));
        assertFalse(HANDLER.processArgument("/C ", mock));
        verify(mock,never()).setClearDestinationDirBefore(anyBoolean());
    
        assertTrue(HANDLER.processArgument("/C", mock));
        verify(mock).setClearDestinationDirBefore(true);
        reset(mock);
        
        assertTrue(HANDLER.processArgument("/c", mock));
        verify(mock).setClearDestinationDirBefore(true);
        reset(mock);
    }
    
    @Override
    public void testName() {
        assertEquals("/C", HANDLER.getKeyName());
    }

    @Override
    public void testDescription() {
        assertDescription(HANDLER);
    }
}
