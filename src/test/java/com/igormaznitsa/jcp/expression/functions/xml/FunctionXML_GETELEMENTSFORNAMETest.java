package com.igormaznitsa.jcp.expression.functions.xml;

import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionXML_GETELEMENTSFORNAMETest extends AbstractFunctionXMLTest {

    private static final FunctionXML_GETELEMENTSFORNAME HANDLER = new FunctionXML_GETELEMENTSFORNAME();

    @Test(expected=IllegalArgumentException.class)
    public void testExecution_ForWrongElement() throws Exception {
        assertNotNull(HANDLER.executeStrStr(SPY_CONTEXT, Value.valueOf("some wrong"), Value.valueOf("nonexist")));
    }
    
    @Test
    public void testExecution_ForNonExistElements() throws Exception {
        assertNotNull(HANDLER.executeStrStr(SPY_CONTEXT, OPENED_DOCUMENT_ROOT, Value.valueOf("nonexist")));
    }
    
    @Test
    public void testExecution_ForExistElements() throws Exception {
        assertNotNull(HANDLER.executeStrStr(SPY_CONTEXT, OPENED_DOCUMENT_ROOT, Value.valueOf("element")));
    }
    
    @Override
    public void testName() {
        assertEquals("xml_getelementsforname",HANDLER.getName());
    }

    @Override
    public void testReference() {
        assertReference(HANDLER);
    }

    @Override
    public void testArity() {
        assertEquals(2,HANDLER.getArity());
    }

    @Override
    public void testAllowedArgumentTypes() {
        assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.STRING,ValueType.STRING}});
    }

    @Override
    public void testResultType() {
        assertEquals(ValueType.STRING, HANDLER.getResultType());
    }
    
}
