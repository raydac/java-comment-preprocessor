package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemPriority;
import org.junit.Test;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import static org.junit.Assert.*;

public class OperatorADDTest extends AbstractOperatorTest {

    private static final OperatorADD HANDLER = new OperatorADD();

    @Override
    public void testExecution() throws Exception {
        assertExecution(Value.valueOf(Long.valueOf(11)), "3+8");
        assertExecution(Value.valueOf("helloworld"), "\"hello\"+\"world\"");
        assertExecution(Value.valueOf(Float.valueOf(2.2f)), "1.2+1");
        assertExecution(Value.valueOf("1test"), "1+\"test\"");
        assertExecution(Value.valueOf("1.2test"), "1.2+\"test\"");
    }

    @Test
    public void testExecution_chain() throws Exception {
        assertExecution(Value.valueOf("1.2.3"), "1+\".\"+2+\".\"+3");
    }

    @Override
    public void testExecution_illegalState() throws Exception {
        assertIllegalStateException("+");
        assertIllegalStateException("+1");
        assertIllegalStateException("2+");
    }

    @Override
    public void testExecution_illegalArgument() throws Exception {
        assertIllegalArgumentException("true+false");
        assertIllegalArgumentException("1+true");
        assertIllegalArgumentException("2.3+false");
    }

    @Override
    public void testArity() {
        assertEquals(2, HANDLER.getArity());
    }

    @Override
    public void testKeyword() {
        assertEquals("+", HANDLER.getKeyword());
    }

    @Override
    public void testReference() {
        assertReference(HANDLER);
    }

    @Override
    public void testPriority() {
        assertEquals(ExpressionStackItemPriority.ARITHMETIC_ADD_SUB, HANDLER.getPriority());
    }
}
