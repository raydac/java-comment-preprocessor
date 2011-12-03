package com.igormaznitsa.jcp.expression.operators;

import com.igormaznitsa.jcp.expression.ExpressionItemPriority;
import com.igormaznitsa.jcp.expression.Value;
import static org.junit.Assert.*;

public class OperatorGREATTest extends AbstractOperatorTest {

    private static final OperatorGREAT HANDLER = new OperatorGREAT();
    
    @Override
    public void testKeyword() {
        assertEquals(">",HANDLER.getKeyword());
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
        assertEquals(ExpressionItemPriority.COMPARISON, HANDLER.getExpressionItemPriority());
    }

    @Override
    public void testExecution() throws Exception {
        assertExecution(Value.BOOLEAN_TRUE, "1>0");
        assertExecution(Value.BOOLEAN_FALSE, "0>0");
        assertExecution(Value.BOOLEAN_TRUE, "\"test\">\"t\"");
        assertExecution(Value.BOOLEAN_TRUE, "1.2>1.1");
        assertExecution(Value.BOOLEAN_FALSE, "1.5>2.3");
    }

    @Override
    public void testExecution_illegalState() throws Exception {
        assertIllegalStateException(">");
        assertIllegalStateException("1>");
        assertIllegalStateException(">0");
    }

    @Override
    public void testExecution_illegalArgument() throws Exception {
        assertIllegalArgumentException("true>\"test\"");
        assertIllegalArgumentException("true>1");
        assertIllegalArgumentException("2.3>\"test\"");
        assertIllegalArgumentException("2.3>false");
        assertIllegalArgumentException("true>false");
        assertIllegalArgumentException("1>false");
    }
    
}
