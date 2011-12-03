package com.igormaznitsa.jcp.cmdline;

import org.junit.Test;
import static org.junit.Assert.*;

public abstract class AbstractCommandLineHandlerTest {

    @Test
    public abstract void testExecution() throws Exception;
    
    @Test
    public abstract void testName();
    
    @Test
    public abstract void testDescription();

    protected void assertDescription(final CommandLineHandler handler){
        assertNotNull("Reference must not be null", handler.getDescription());
        assertFalse("Reference must not be empty one",handler.getDescription().isEmpty());
        assertTrue("Reference length must be great than 10 chars",handler.getDescription().length()>10);
    }
    
}
