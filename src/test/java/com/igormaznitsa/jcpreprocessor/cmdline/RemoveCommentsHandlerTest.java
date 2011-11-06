package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RemoveCommentsHandlerTest extends AbstractCommandLineHandlerTest {

    private static final RemoveCommentsHandler HANDLER = new RemoveCommentsHandler();
    
    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = mock(PreprocessorContext.class);
        
        assertFalse(HANDLER.processArgument(null, mock));
        assertFalse(HANDLER.processArgument("", mock));
        assertFalse(HANDLER.processArgument("/r:", mock));
        assertFalse(HANDLER.processArgument("/R:", mock));
        assertFalse(HANDLER.processArgument("/RR", mock));
        
        assertTrue(HANDLER.processArgument("/r", mock));
        verify(mock).setRemovingComments(true);
    }

    @Override
    public void testName() {
        assertEquals("/R", HANDLER.getKeyName());
    }

    @Override
    public void testDescription() {
        assertDescription(HANDLER);
    }
}
