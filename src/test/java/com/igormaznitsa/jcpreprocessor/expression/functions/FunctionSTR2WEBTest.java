package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionSTR2WEBTest extends AbstractFunctionTest {

    private static final FunctionSTR2WEB HANDLER = new FunctionSTR2WEB();
    
    @Test
    public void testExecution_Str() throws Exception {
        assertFunction("str2web(\"<hello>\")", Value.valueOf("&lt;hello&gt;"));
    }
    
    @Test
    public void testExecution_wrongCases() throws Exception {
        assertFunctionException("str2web()");
        assertFunctionException("str2web(1,2)");
        assertFunctionException("str2web(true)");
        assertFunctionException("str2web(3)");
    }
    
    @Override
    public void testName() {
        assertEquals("str2web", HANDLER.getName());
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
