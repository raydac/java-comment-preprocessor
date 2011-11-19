package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import org.mockito.Mockito;
import static org.junit.Assert.*;

public class OutCharsetHandlerTest extends AbstractCommandLineHandlerTest {

    private static final OutCharsetHandler HANDLER = new OutCharsetHandler();

    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = Mockito.mock(PreprocessorContext.class);
        assertFalse(HANDLER.processArgument(null, mock));
        assertFalse(HANDLER.processArgument("/o:UUU", mock));
        assertFalse(HANDLER.processArgument("/TT:", mock));
        assertFalse(HANDLER.processArgument("/tT", mock));
        assertTrue(HANDLER.processArgument("/tt:HELLOWORLD", mock));
        Mockito.verify(mock).setInCharacterEncoding("HELLOWORLD");

        Mockito.reset(mock);
        
        assertTrue(HANDLER.processArgument("/TT:NEW", mock));
        Mockito.verify(mock).setInCharacterEncoding("NEW");
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
