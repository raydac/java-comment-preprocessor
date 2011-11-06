package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionROUNDTest extends AbstractFunctionTest {

    private static final FunctionROUND HANDLER = new FunctionROUND();

    @Test
    public void testExecution_Float() throws Exception {
        assertFunction("round(4.7)", Value.valueOf(Long.valueOf(5L)));
        assertFunction("round(3.1+3.6)", Value.valueOf(Long.valueOf(7L)));
    }
    
    @Test
    public void testExecution_Int() throws Exception {
        assertFunction("round(4)", Value.valueOf(Long.valueOf(4L)));
    }
    
    @Test
    public void testExecution_wrongCases() throws Exception {
        assertFunctionException("round(true)");
        assertFunctionException("round(\"aaa\")");
        assertFunctionException("round()");
        assertFunctionException("round(0.3,2.1)");
    }
    
    
    @Override
    public void testName() {
        assertEquals("round", HANDLER.getName());
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
        assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.INT},{ValueType.FLOAT}});
    }

    @Override
    public void testResultType() {
        assertEquals(ValueType.INT, HANDLER.getResultType());
    }
    
}
