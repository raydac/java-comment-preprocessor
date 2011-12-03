package com.igormaznitsa.jcp.expression.functions.xml;

import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionXML_GETELEMENTTEXTTest extends AbstractFunctionXMLTest {

    private static final FunctionXML_GETELEMENTTEXT HANDLER = new FunctionXML_GETELEMENTTEXT();

    @Test(expected=IllegalArgumentException.class)
    public void testExecution_IncompatibleCachedObjectId() throws Exception {
        HANDLER.executeStr(SPY_CONTEXT, OPENED_DOCUMENT_ID);
    }
    
    @Test
    public void testExecution() throws Exception {
        final Value root = HANDLER.executeStr(SPY_CONTEXT,  new FunctionXML_GETROOT().executeStr(SPY_CONTEXT,OPENED_DOCUMENT_ID));
        assertEquals("\n\nelem1\nelem2\nelem3\n<test>\n\n",root.asString());
    }
    
    @Override
    public void testName() {
        assertEquals("xml_getelementtext",HANDLER.getName());
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
