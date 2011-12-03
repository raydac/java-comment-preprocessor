package com.igormaznitsa.jcp.expression.functions.xml;

import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionXML_ELEMENTATTest extends AbstractFunctionXMLTest {

    private static final FunctionXML_ELEMENTAT HANDLER = new FunctionXML_ELEMENTAT();
    private static final FunctionXML_GETELEMENTTEXT GETTEXT = new FunctionXML_GETELEMENTTEXT();

    @Test(expected=IllegalArgumentException.class)
    public void testExecution_WrongElementId() throws Exception {
        HANDLER.executeStrInt(SPY_CONTEXT, Value.valueOf("12qwewqe"), Value.INT_ZERO);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testExecution_WrongIndex() throws Exception {
        final Value elementList = new FunctionXML_GETELEMENTSFORNAME().executeStrStr(SPY_CONTEXT, OPENED_DOCUMENT_ROOT, Value.valueOf("element"));
        HANDLER.executeStrInt(SPY_CONTEXT, elementList, Value.valueOf(Long.valueOf(-1)));
    }
    
    @Test
    public void testExecution() throws Exception {
        final Value elementList = new FunctionXML_GETELEMENTSFORNAME().executeStrStr(SPY_CONTEXT, OPENED_DOCUMENT_ROOT, Value.valueOf("element"));
        assertEquals("elem1",GETTEXT.executeStr(SPY_CONTEXT, HANDLER.executeStrInt(SPY_CONTEXT, elementList, Value.INT_ZERO)).asString());
        assertEquals("elem2",GETTEXT.executeStr(SPY_CONTEXT, HANDLER.executeStrInt(SPY_CONTEXT, elementList, Value.INT_ONE)).asString());
        assertEquals("elem3",GETTEXT.executeStr(SPY_CONTEXT, HANDLER.executeStrInt(SPY_CONTEXT, elementList, Value.INT_TWO)).asString());
        assertEquals("<test>",GETTEXT.executeStr(SPY_CONTEXT, HANDLER.executeStrInt(SPY_CONTEXT, elementList, Value.INT_THREE)).asString());
    }
    
    @Override
    public void testName() {
        assertEquals("xml_elementat", HANDLER.getName());
    }

    @Override
    public void testReference() {
        assertReference(HANDLER);
    }

    @Override
    public void testArity() {
        assertEquals(2, HANDLER.getArity());
    }

    @Override
    public void testAllowedArgumentTypes() {
        assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.STRING,ValueType.INT}});
    }

    @Override
    public void testResultType() {
        assertEquals(ValueType.STRING, HANDLER.getResultType());
    }

}
