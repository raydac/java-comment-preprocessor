package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

public class InCharsetHandlerTest extends AbstractCommandLineHandlerTest {

    private static final InCharsetHandler HANDLER = new InCharsetHandler();

    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext mock = Mockito.mock(PreprocessorContext.class);
        assertFalse(HANDLER.processArgument(null, mock));
        assertFalse(HANDLER.processArgument("/o:UUU", mock));
        assertFalse(HANDLER.processArgument("/T:", mock));
        assertFalse(HANDLER.processArgument("/t", mock));
        assertTrue(HANDLER.processArgument("/t:HELLOWORLD", mock));
        Mockito.verify(mock).setInCharacterEncoding("HELLOWORLD");

        Mockito.reset(mock);
        
        assertTrue(HANDLER.processArgument("/T:NEW", mock));
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
