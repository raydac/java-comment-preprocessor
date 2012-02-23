package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class DestinationDirectoryHandlerTest extends AbstractCommandLineHandlerTest {

    private static final DestinationDirectoryHandler HANDLER = new DestinationDirectoryHandler();

    @Override
    public void testThatTheHandlerInTheHandlerList() {
        assertHandlerInTheHandlerList(HANDLER);
    }

    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = mock(PreprocessorContext.class);
        assertFalse(HANDLER.processCommandLineKey(null, mock));
        assertFalse(HANDLER.processCommandLineKey("/s:", mock));
        assertFalse(HANDLER.processCommandLineKey("/O:", mock));
        assertTrue(HANDLER.processCommandLineKey("/O:test", mock));
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
