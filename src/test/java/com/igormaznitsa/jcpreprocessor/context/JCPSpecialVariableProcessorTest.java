package com.igormaznitsa.jcpreprocessor.context;

import com.igormaznitsa.jcpreprocessor.InfoHelper;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import org.junit.Test;
import static org.junit.Assert.*;

public class JCPSpecialVariableProcessorTest {
    
    @Test
    public void testReadVariable(){
        assertEquals("Must be equals", InfoHelper.getVersion(), new JCPSpecialVariableProcessor().getVariable("jcp.version", null).asString());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testReadUnknownVariable(){
        new JCPSpecialVariableProcessor().getVariable("jcp.version2", null);
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void testWriteDisallowed() {
        new JCPSpecialVariableProcessor().setVariable("jcp.version", Value.INT_ONE, null);
    }
}
