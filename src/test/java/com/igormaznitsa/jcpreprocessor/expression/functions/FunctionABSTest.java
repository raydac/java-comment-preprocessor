package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionABSTest extends AbstractFunctionTest {

    private static final FunctionABS HANDLER = new FunctionABS();
    
    @Test
    public void testExecution_Int() throws Exception {
        assertFunction("abs(-10)", Value.valueOf(Long.valueOf(10)));
        assertFunction("abs(1-3*2)", Value.valueOf(Long.valueOf(5)));
    }
    
    @Test
    public void testExecution_Float() throws Exception {
        assertFunction("abs(-10.5)", Value.valueOf(Float.valueOf(10.5f)));
    }
    
    public void testExecution_wrongCases() throws Exception {
        assertFunctionException("abs(\"test\")");
        assertFunctionException("abs()");
        assertFunctionException("abs(false)");
        assertFunctionException("abs(1,2,3)");
    }
    
    @Override
    public void testName() {
        assertEquals("abs",HANDLER.getName());
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
        assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.INT},{ValueType.FLOAT}});
    }

    @Override
    public void testResultType() {
        assertEquals(ValueType.ANY, HANDLER.getResultType());
    }
    
}
