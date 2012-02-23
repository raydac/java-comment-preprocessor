package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FileExtensionsHandlerTest extends AbstractCommandLineHandlerTest {
 
    private static final FileExtensionsHandler HANDLER = new FileExtensionsHandler();

    @Override
    public void testThatTheHandlerInTheHandlerList() {
        assertHandlerInTheHandlerList(HANDLER);
    }

    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = mock(PreprocessorContext.class);
    
        assertFalse(HANDLER.processCommandLineKey(null, mock));
        assertFalse(HANDLER.processCommandLineKey("", mock));
        assertFalse(HANDLER.processCommandLineKey("/f:", mock));
        assertFalse(HANDLER.processCommandLineKey("/f", mock));
        assertFalse(HANDLER.processCommandLineKey("/F:", mock));

        verify(mock,never()).setExcludedFileExtensions(anyString());
        
        assertTrue(HANDLER.processCommandLineKey("/f:rrr,Ggg,bBb", mock));
        verify(mock).setProcessingFileExtensions("rrr,Ggg,bBb");
    }

    @Override
    public void testName() {
        assertEquals("/F:",HANDLER.getKeyName());
    }

    @Override
    public void testDescription() {
        assertDescription(HANDLER);
    }
    
}
