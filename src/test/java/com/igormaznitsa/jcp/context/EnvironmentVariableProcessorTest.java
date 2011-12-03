package com.igormaznitsa.jcp.context;

import com.igormaznitsa.jcp.expression.Value;
import org.junit.Test;
import static org.junit.Assert.*;

public class EnvironmentVariableProcessorTest {
    
    @Test
    public void testReadVariable(){
        final String javaVersion = System.getProperty("java.version");
        final String osName = System.getProperty("os.name");
        
        assertNotNull("Must not be null",javaVersion);
        assertNotNull("Must not be null",osName);
        
        final EnvironmentVariableProcessor test = new EnvironmentVariableProcessor();
        
        assertEquals("Must be equals", javaVersion, test.getVariable("env.java.version", null).asString());
        assertEquals("Must be equals", osName, test.getVariable("env.os.name", null).asString());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testReadUnknownVariable(){
        new EnvironmentVariableProcessor().getVariable("kjhaksjdhksajqwoiueoqiwue", null);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testWriteVariable(){
        new EnvironmentVariableProcessor().setVariable("kjhaksjdhksajqwoiueoqiwue", Value.BOOLEAN_FALSE, null);
    }
}
