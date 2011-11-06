package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import static org.junit.Assert.*;

public class HelpHandlerTest extends AbstractCommandLineHandlerTest {

    private static final HelpHandler HANDLER = new HelpHandler();
    
    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext context = Mockito.mock(PreprocessorContext.class);
        assertFalse(HANDLER.processArgument(null, context));
        assertFalse(HANDLER.processArgument("", context));
        assertFalse(HANDLER.processArgument("/HH", context));
        assertFalse(HANDLER.processArgument("/??", context));
        assertFalse(HANDLER.processArgument("-??", context));
        
        assertTrue(HANDLER.processArgument("/?", context));
        assertTrue(HANDLER.processArgument("/h", context));
        assertTrue(HANDLER.processArgument("-H", context));
        assertTrue(HANDLER.processArgument("-?", context));
    }

    @Override
    public void testName() {
        assertEquals("/H,/?,-H,-?",HANDLER.getKeyName());
    }

    @Override
    public void testDescription() {
        assertDescription(HANDLER);
    }
}
