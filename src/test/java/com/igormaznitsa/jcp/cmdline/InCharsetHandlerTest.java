package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

public class InCharsetHandlerTest extends AbstractCommandLineHandlerTest {

    private static final InCharsetHandler HANDLER = new InCharsetHandler();

    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = Mockito.mock(PreprocessorContext.class);
        assertFalse(HANDLER.processCommandLineKey(null, mock));
        assertFalse(HANDLER.processCommandLineKey("/o:UUU", mock));
        assertFalse(HANDLER.processCommandLineKey("/T:", mock));
        assertFalse(HANDLER.processCommandLineKey("/t", mock));
        assertTrue(HANDLER.processCommandLineKey("/t:HELLOWORLD", mock));
        Mockito.verify(mock).setInCharacterEncoding("HELLOWORLD");

        Mockito.reset(mock);
        
        assertTrue(HANDLER.processCommandLineKey("/T:NEW", mock));
        Mockito.verify(mock).setInCharacterEncoding("NEW");
    }

    @Override
    public void testName() {
        assertEquals("/T:", HANDLER.getKeyName());
    }

    @Override
    public void testDescription() {
        assertDescription(HANDLER);
    }
}
