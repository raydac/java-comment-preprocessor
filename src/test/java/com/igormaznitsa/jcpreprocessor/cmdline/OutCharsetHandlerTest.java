package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import org.mockito.Mockito;
import static org.junit.Assert.*;

public class OutCharsetHandlerTest extends AbstractCommandLineHandlerTest {

    private static final OutCharsetHandler HANDLER = new OutCharsetHandler();

    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = Mockito.mock(PreprocessorContext.class);
        assertFalse(HANDLER.processCommandLineKey(null, mock));
        assertFalse(HANDLER.processCommandLineKey("/o:UUU", mock));
        assertFalse(HANDLER.processCommandLineKey("/TT:", mock));
        assertFalse(HANDLER.processCommandLineKey("/tT", mock));
        assertTrue(HANDLER.processCommandLineKey("/tt:HELLOWORLD", mock));
        Mockito.verify(mock).setOutCharacterEncoding("HELLOWORLD");

        Mockito.reset(mock);
        
        assertTrue(HANDLER.processCommandLineKey("/TT:NEW", mock));
        Mockito.verify(mock).setOutCharacterEncoding("NEW");
    }

    @Override
    public void testName() {
        assertEquals("/TT:", HANDLER.getKeyName());
    }

    @Override
    public void testDescription() {
        assertDescription(HANDLER);
    }
}
