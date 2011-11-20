package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import static org.junit.Assert.*;

public class OperatorNOTTest extends AbstractOperatorTest {

    private static final OperatorNOT HANDLER = new OperatorNOT();
    
    @Override
    public void testKeyword() {
        assertEquals("!", HANDLER.getKeyword());
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
    public void testPriority() {
        assertEquals(ExpressionItemPriority.FUNCTION,HANDLER.getExpressionItemPriority());
    }

    @Override
    public void testExecution() throws Exception {
        assertExecution(Value.BOOLEAN_TRUE, "!false");
        assertExecution(Value.BOOLEAN_FALSE, "!true");
        assertExecution(Value.valueOf(Long.valueOf(0xFFFFFFFFFFFFFFFFL^10L)), "!10");
    }

    @Override
    public void testExecution_illegalState() throws Exception {
        assertIllegalStateException("!");
//TODO        assertIllegalStateException("2!");
    }

    @Override
    public void testExecution_illegalArgument() throws Exception {
        assertIllegalArgumentException("!\"test\"");
        assertIllegalArgumentException("!3.2");
    }
    
}
