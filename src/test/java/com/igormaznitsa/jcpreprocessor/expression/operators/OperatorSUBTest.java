package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import static org.junit.Assert.*;

public class OperatorSUBTest extends AbstractOperatorTest {

    private static final OperatorSUB HANDLER = new OperatorSUB();
    
    @Override
    public void testKeyword() {
        assertEquals("-", HANDLER.getKeyword());
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
        assertEquals(ExpressionStackItemPriority.ARITHMETIC_ADD_SUB, HANDLER.getPriority());
    }

    @Override
    public void testExecution() throws Exception {
        assertExecution(Value.INT_ZERO, "10-10");
        assertExecution(Value.INT_THREE, "7-4");
        assertExecution(Value.valueOf(Float.valueOf(1.5f-1.2f)), "1.5-1.2");
        assertExecution(Value.valueOf(Float.valueOf(1.0f-1.2f)), "1-1.2");
        assertExecution(Value.valueOf(Float.valueOf(-1.2f)), "-1.2");
    }

    @Override
    public void testExecution_illegalState() throws Exception {
        assertIllegalStateException("-");
//TODO        assertIllegalStateException("1-");
    }

    @Override
    public void testExecution_illegalArgument() throws Exception {
        assertIllegalArgumentException("1-\"test\"");
        assertIllegalArgumentException("1-true");
        assertIllegalArgumentException("true-1");
        assertIllegalArgumentException("true-1.1");
        assertIllegalArgumentException("true-false");
    }
    
}
