package com.igormaznitsa.jcp.expression.operators;

import com.igormaznitsa.jcp.expression.ExpressionItemPriority;
import com.igormaznitsa.jcp.expression.Value;
import org.junit.Test;
import static org.junit.Assert.*;

public class OperatorDIVTest extends AbstractOperatorTest {

    private static final OperatorDIV HANDLER = new OperatorDIV();
    
    @Override
    public void testExecution() throws Exception {
        assertExecution(Value.valueOf(Long.valueOf(5L)), "10/2");
        assertExecution(Value.valueOf(Float.valueOf(1.5f)), "3.0/2");
        assertExecution(Value.valueOf(Long.valueOf(1L)), "3/2");
    }
    
    @Test
    public void testExecution_chain() throws Exception {
        assertExecution(Value.valueOf(8L), "160/4/5");
    }
    
    @Override
    public void testExecution_illegalState() {
        assertIllegalStateException("/");
        assertIllegalStateException("1/");
        assertIllegalStateException("/2.2");
    }
    
    @Override
    public void testExecution_illegalArgument() {
        assertIllegalArgumentException("1/true");
        assertIllegalArgumentException("false/3");
        assertIllegalArgumentException("\"hello\"/2.2");
        assertIllegalArgumentException("1/\"hello\"");
    }
    
    @Test(expected=ArithmeticException.class)
    public void testExecution_arithmeticException() throws Exception {
        assertExecution(Value.INT_ZERO, "1/0");
    }
    
    @Override
    public void testKeyword() {
        assertEquals("/", HANDLER.getKeyword());
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
    public void testPriority() {
        assertEquals(ExpressionItemPriority.ARITHMETIC_MUL_DIV_MOD, HANDLER.getExpressionItemPriority());
    }
}
