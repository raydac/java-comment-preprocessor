package com.igormaznitsa.jcp.expression.functions.xml;

import org.junit.Before;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionXML_GETELEMENTNAMETest extends AbstractFunctionXMLTest {

    private static final FunctionXML_GETELEMENTNAME HANDLER = new FunctionXML_GETELEMENTNAME();
    
    @Test(expected=IllegalArgumentException.class)
    public void testExecution_WrongElementId() throws Exception {
        HANDLER.executeStr(SPY_CONTEXT, Value.valueOf("nonexistelementaaaaaaaaaaa"));
    }
    
    @Test
    public void testExecution_RootElement() throws Exception {
        assertEquals("root",HANDLER.executeStr(SPY_CONTEXT, OPENED_DOCUMENT_ROOT).asString());
    }
    
    @Override
    public void testName() {
        assertEquals("xml_getelementname", HANDLER.getName());
    }

    @Override
    public void testReference() {
        assertReference(HANDLER);
    }

    @Override
    public void testArity() {
        assertEquals(1, HANDLER.getArity());
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
