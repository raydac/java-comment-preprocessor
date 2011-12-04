package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SourceDirectoryHandlerTest extends AbstractCommandLineHandlerTest {

    private static final SourceDirectoryHandler HANDLER = new SourceDirectoryHandler();
    
    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = mock(PreprocessorContext.class);
        
        assertFalse(HANDLER.processCommandLineKey(null, mock));
        assertFalse(HANDLER.processCommandLineKey("", mock));
        assertFalse(HANDLER.processCommandLineKey("/i:", mock));
        assertFalse(HANDLER.processCommandLineKey("/I:", mock));
        assertFalse(HANDLER.processCommandLineKey("/II", mock));
        
        assertTrue(HANDLER.processCommandLineKey("/i:testdir", mock));
        verify(mock).setSourceDirectories("testdir");
    }

    @Override
    public void testName() {
        assertEquals("/I:", HANDLER.getKeyName());
    }

    @Override
    public void testDescription() {
        assertDescription(HANDLER);
    }
    
}
