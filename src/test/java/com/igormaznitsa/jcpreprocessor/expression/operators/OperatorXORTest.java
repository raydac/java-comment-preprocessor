package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import static org.junit.Assert.*;

public class OperatorXORTest extends AbstractOperatorTest {

    private static final OperatorXOR HANDLER = new OperatorXOR();
    
    @Override
    public void testKeyword() {
        assertEquals("^",HANDLER.getKeyword());
    }

    @Override
    public void testReference() {
        assertReference(HANDLER);
    }

    @Override
    public void testArity() {
        assertEquals(2,HANDLER.getArity());
    }

    @Override
    public void testPriority() {
        assertEquals(ExpressionItemPriority.LOGICAL,HANDLER.getExpressionItemPriority());
    }

    @Override
    public void testExecution() throws Exception {
        assertExecution(Value.INT_ZERO, Long.MAX_VALUE+"^"+Long.MAX_VALUE);
        assertExecution(Value.BOOLEAN_FALSE, "false^false");
        assertExecution(Value.BOOLEAN_TRUE, "false^true");
        assertExecution(Value.BOOLEAN_TRUE, "true^false");
        assertExecution(Value.BOOLEAN_FALSE, "true^true");
    }

    @Override
    public void testExecution_illegalState() throws Exception {
        assertIllegalStateException("^");
        assertIllegalStateException("1^");
        assertIllegalStateException("^2");
    }

    @Override
    public void testExecution_illegalArgument() throws Exception {
        assertIllegalArgumentException("\"test\"^1");
        assertIllegalArgumentException("1.3^1");
        assertIllegalArgumentException("2^\"test\"");
        assertIllegalArgumentException("2^1.2");
        assertIllegalArgumentException("2.1^1.2");
    }
}
