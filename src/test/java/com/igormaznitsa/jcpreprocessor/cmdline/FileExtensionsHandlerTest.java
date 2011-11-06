package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FileExtensionsHandlerTest extends AbstractCommandLineHandlerTest {
 
    private static final FileExtensionsHandler HANDLER = new FileExtensionsHandler();

    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = mock(PreprocessorContext.class);
    
        assertFalse(HANDLER.processArgument(null, mock));
        assertFalse(HANDLER.processArgument("", mock));
        assertFalse(HANDLER.processArgument("/f:", mock));
        assertFalse(HANDLER.processArgument("/f", mock));
        assertFalse(HANDLER.processArgument("/F:", mock));

        verify(mock,never()).setExcludedFileExtensions(anyString());
        
        assertTrue(HANDLER.processArgument("/f:rrr,Ggg,bBb", mock));
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
