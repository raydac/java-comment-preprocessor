package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import static org.junit.Assert.*;

public class OperatorNOTEQUTest extends AbstractOperatorTest {

    private static final OperatorNOTEQU HANDLER = new OperatorNOTEQU();
    
    @Override
    public void testKeyword() {
        assertEquals("!=", HANDLER.getKeyword());
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
        assertEquals(ExpressionItemPriority.COMPARISON, HANDLER.getExpressionItemPriority());
    }

    @Override
    public void testExecution() throws Exception {
        assertExecution(Value.BOOLEAN_TRUE, "1!=0");
        assertExecution(Value.BOOLEAN_TRUE, "true!=false");
        assertExecution(Value.BOOLEAN_TRUE, "1.3!=1.2");
        assertExecution(Value.BOOLEAN_TRUE, "\"test\"!=\"test2\"");
        assertExecution(Value.BOOLEAN_FALSE, "1.2!=1.2");
        assertExecution(Value.BOOLEAN_FALSE, "1!=1");
        assertExecution(Value.BOOLEAN_FALSE, "true!=true");
        assertExecution(Value.BOOLEAN_FALSE, "\"test\"!=\"test\"");
        assertExecution(Value.BOOLEAN_TRUE, "1!=1.2");
    }

    @Override
    public void testExecution_illegalState() throws Exception {
        assertIllegalStateException("!=");
        assertIllegalStateException("true!=");
        assertIllegalStateException("!=2");
    }

    @Override
    public void testExecution_illegalArgument() throws Exception {
        assertIllegalArgumentException("true!=\"hello\"");
        assertIllegalArgumentException("true!=1.2");
    }
    
}
