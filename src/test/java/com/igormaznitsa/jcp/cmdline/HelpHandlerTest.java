package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import static org.junit.Assert.*;

public class HelpHandlerTest extends AbstractCommandLineHandlerTest {

    private static final HelpHandler HANDLER = new HelpHandler();
    
    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext context = Mockito.mock(PreprocessorContext.class);
        assertFalse(HANDLER.processCommandLineKey(null, context));
        assertFalse(HANDLER.processCommandLineKey("", context));
        assertFalse(HANDLER.processCommandLineKey("/HH", context));
        assertFalse(HANDLER.processCommandLineKey("/??", context));
        assertFalse(HANDLER.processCommandLineKey("-??", context));
        
        assertTrue(HANDLER.processCommandLineKey("/?", context));
        assertTrue(HANDLER.processCommandLineKey("/h", context));
        assertTrue(HANDLER.processCommandLineKey("-H", context));
        assertTrue(HANDLER.processCommandLineKey("-?", context));
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
