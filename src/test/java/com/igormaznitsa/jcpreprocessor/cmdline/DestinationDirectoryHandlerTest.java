package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class DestinationDirectoryHandlerTest extends AbstractCommandLineHandlerTest {

    private static final DestinationDirectoryHandler HANDLER = new DestinationDirectoryHandler();

    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = mock(PreprocessorContext.class);
        assertFalse(HANDLER.processArgument(null, mock));
        assertFalse(HANDLER.processArgument("/s:", mock));
        assertFalse(HANDLER.processArgument("/O:", mock));
        assertTrue(HANDLER.processArgument("/O:test", mock));
        verify(mock).setDestinationDirectory("test");
    }

    @Override
    public void testName() {
        assertEquals("/O:", HANDLER.getKeyName());
    }

    @Override
    public void testDescription() {
        assertDescription(HANDLER);
    }
    
}
