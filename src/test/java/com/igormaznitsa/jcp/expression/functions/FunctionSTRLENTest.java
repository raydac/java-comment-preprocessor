package com.igormaznitsa.jcp.expression.functions;

import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionSTRLENTest extends AbstractFunctionTest {

    private static final FunctionSTRLEN HANDLER = new FunctionSTRLEN();

    @Test
    public void testExecution_Str() throws Exception {
        assertFunction("strlen(\"hello world\")", Value.valueOf(Long.valueOf(11L)));
    }
    
    @Test
    public void testExecution_wrongCases() throws Exception {
        assertFunctionException("strlen()");
        assertFunctionException("strlen(11)");
        assertFunctionException("strlen(true)");
        assertFunctionException("strlen(1,2)");
        assertFunctionException("strlen(,)");
    }
    
    @Override
    public void testName() {
        assertEquals("strlen",HANDLER.getName());
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
