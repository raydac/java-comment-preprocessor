package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ExcludedFileExtensionsHandlerTest extends AbstractCommandLineHandlerTest {

    private static final ExcludedFileExtensionsHandler HANDLER = new ExcludedFileExtensionsHandler();

    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = mock(PreprocessorContext.class);
    
        assertFalse(HANDLER.processCommandLineKey(null, mock));
        assertFalse(HANDLER.processCommandLineKey("", mock));
        assertFalse(HANDLER.processCommandLineKey("/ef:", mock));
        assertFalse(HANDLER.processCommandLineKey("/ef", mock));
        assertFalse(HANDLER.processCommandLineKey("/EF:", mock));

        verify(mock,never()).setExcludedFileExtensions(anyString());
        
        assertTrue(HANDLER.processCommandLineKey("/ef:rrr,Ggg,bBb", mock));
        verify(mock).setExcludedFileExtensions("rrr,Ggg,bBb");
    }

    @Override
    public void testName() {
        assertEquals("/EF:",HANDLER.getKeyName());
    }

    @Override
    public void testDescription() {
        assertDescription(HANDLER);
    }
    
}
