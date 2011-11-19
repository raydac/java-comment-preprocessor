package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import static org.junit.Assert.*;

public class OperatorORTest extends AbstractOperatorTest {

    private static final OperatorOR HANDLER = new OperatorOR();
    
    @Override
    public void testKeyword() {
        assertEquals("||", HANDLER.getKeyword());
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
        assertEquals(ExpressionStackItemPriority.LOGICAL, HANDLER.getPriority());
    }

    @Override
    public void testExecution() throws Exception {
        assertExecution(Value.BOOLEAN_FALSE, "false||false");
        assertExecution(Value.BOOLEAN_TRUE, "true||false");
        assertExecution(Value.BOOLEAN_TRUE, "false||true");
        assertExecution(Value.BOOLEAN_TRUE, "true||true");
        assertExecution(Value.valueOf(Long.valueOf(3L)), "1||2");
    }

    @Override
    public void testExecution_illegalState() throws Exception {
        assertIllegalStateException("||");
        assertIllegalStateException("true||");
        assertIllegalStateException("||false");
    }

    @Override
    public void testExecution_illegalArgument() throws Exception {
        assertIllegalArgumentException("\"test\"||true");
        assertIllegalArgumentException("true||1");
        assertIllegalArgumentException("1.2||1.1");
    }
}
