package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemPriority;
import org.junit.Test;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import static org.junit.Assert.*;

public class OperatorMODTest extends AbstractOperatorTest {

    private static final OperatorMOD HANDLER = new OperatorMOD();
    
    @Override
    public void testExecution() throws Exception {
        assertExecution(Value.valueOf(Long.valueOf(3L)), "3%8");
        assertExecution(Value.valueOf(Long.valueOf(2L)), "8%3");
        assertExecution(Value.valueOf(Float.valueOf(2.5f%1.1f)), "2.5%1.1");
    }
    
    @Override
    public void testExecution_illegalState() throws Exception {
        assertIllegalStateException("%");
        assertIllegalStateException("%1");
        assertIllegalStateException("2%");
    }
    
    @Override
    public void testExecution_illegalArgument() throws Exception {
        assertIllegalArgumentException("true%false");
        assertIllegalArgumentException("1%true");
    }
    
    @Test(expected=ArithmeticException.class)
    public void testExecution_arithmeticException() throws Exception {
        assertExecution(Value.INT_ZERO, "3%0");
    }
    
    @Override
    public void testArity() {
        assertEquals(2, HANDLER.getArity());
    }

    @Override
    public void testKeyword() {
        assertEquals("%", HANDLER.getKeyword());
    }

    @Override
    public void testReference() {
        assertReference(HANDLER);
    }

    @Override
    public void testPriority() {
        assertEquals(ExpressionStackItemPriority.ARITHMETIC_MUL_DIV_MOD, HANDLER.getPriority());
    }

    
}
