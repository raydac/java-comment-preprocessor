package com.igormaznitsa.jcp.expression.functions;

import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionISSUBSTRTest extends AbstractFunctionTest {

    private static final FunctionISSUBSTR HANDLER = new FunctionISSUBSTR();
    
    @Test
    public void testExecution_StrStr() throws Exception {
        assertFunction("issubstr(\"test\",\"onetesttwo\")", Value.BOOLEAN_TRUE);
        assertFunction("issubstr(\"Test\",\"onetesttwo\")", Value.BOOLEAN_TRUE);
        assertFunction("issubstr(\"test\",\"oneTesttwo\")", Value.BOOLEAN_TRUE);
        assertFunction("issubstr(\"test\",\"one\")", Value.BOOLEAN_FALSE);
        assertFunction("issubstr(\"\",\"one\")", Value.BOOLEAN_TRUE);
        assertFunction("issubstr(\"\",\"\")", Value.BOOLEAN_TRUE);
    }
 
    @Test
    public void testExecution_StrStr_wrongCases() throws Exception {
        assertFunctionException("issubstr()");
        assertFunctionException("issubstr(\"test\")");
        assertFunctionException("issubstr(,)");
        assertFunctionException("issubstr(1,\"ttt\")");
        assertFunctionException("issubstr(false,\"ttt\")");
        assertFunctionException("issubstr(false,true)");
        assertFunctionException("issubstr(\"d\",1)");
    }
    
    @Override
    public void testName() {
        assertEquals("issubstr",HANDLER.getName());
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
        assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.STRING, ValueType.STRING}});
    }

    @Override
    public void testResultType() {
        assertEquals(ValueType.BOOLEAN, HANDLER.getResultType());
    }
}
