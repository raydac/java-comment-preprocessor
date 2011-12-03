package com.igormaznitsa.jcp.expression.functions.xml;

import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionXML_GETROOTTest extends AbstractFunctionXMLTest {

    private static final FunctionXML_GETROOT HANDLER = new FunctionXML_GETROOT();

    @Test(expected=IllegalArgumentException.class)
    public void testExecution_WrongDocId() throws Exception {
        HANDLER.executeStr(SPY_CONTEXT, Value.valueOf("jlskjlasjdsa123213213"));
    }
    
    @Test
    public void testExecution() throws Exception {
        assertEquals(OPENED_DOCUMENT_ID.asString()+"_root",HANDLER.executeStr(SPY_CONTEXT, OPENED_DOCUMENT_ID).asString());
    }
    
    @Override
    public void testName() {
        assertEquals("xml_getroot",HANDLER.getName());
    }

    @Override
    public void testReference() {
        assertReference(HANDLER);
    }

    @Override
    public void testArity() {
        assertEquals(1,HANDLER.getArity());
    }

    @Override
    public void testAllowedArgumentTypes() {
        assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.STRING}});
    }

    @Override
    public void testResultType() {
        assertEquals(ValueType.STRING, HANDLER.getResultType());
    }
    
}
