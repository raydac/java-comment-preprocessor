package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionSTR2INTTest extends AbstractFunctionTest {

    private static final FunctionSTR2INT HANDLER = new FunctionSTR2INT();
    
    @Test
    public void testExecute_Str() throws Exception {
        assertFunction("str2int(\"100\")", Value.valueOf(Long.valueOf(100L)));
        assertFunction("str2int(\"0\")", Value.INT_ZERO);
    }
    
    @Test
    public void testExecute_wrongCase() throws Exception {
        assertFunctionException("str2int(true)");
        assertFunctionException("str2int(0.3)");
        assertFunctionException("str2int(1,2)");
    }
    
    @Override
    public void testName() {
        assertEquals("str2int", HANDLER.getName());
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
        assertEquals(ValueType.INT, HANDLER.getResultType());
    }
    
}
