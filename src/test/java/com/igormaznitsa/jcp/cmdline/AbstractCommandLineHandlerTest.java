package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.JCPreprocessor;
import static org.junit.Assert.*;
import org.junit.Test;

public abstract class AbstractCommandLineHandlerTest {

    @Test
    public abstract void testExecution() throws Exception;
    
    @Test
    public abstract void testName();
    
    @Test
    public abstract void testDescription();

    @Test
    public abstract void testThatTheHandlerInTheHandlerList();
    
    protected void assertDescription(final CommandLineHandler handler){
        assertNotNull("Reference must not be null", handler.getDescription());
        assertFalse("Reference must not be empty one",handler.getDescription().isEmpty());
        assertTrue("Reference length must be great than 10 chars",handler.getDescription().length()>10);
    }
    
    protected void assertHandlerInTheHandlerList(final CommandLineHandler handler) {
        for (final CommandLineHandler h : JCPreprocessor.getCommandLineHandlers()) {
            if (handler.getClass() == h.getClass()) {
                return;
            }
        }
        fail("There is not the handler in the common command line handler list");
    }
    
}
