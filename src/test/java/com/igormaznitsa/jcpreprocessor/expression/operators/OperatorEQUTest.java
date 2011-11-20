package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import static org.junit.Assert.*;

public class OperatorEQUTest extends AbstractOperatorTest {

    private static final OperatorEQU HANDLER = new OperatorEQU();

    @Override
    public void testExecution() throws Exception {
        assertExecution(Value.BOOLEAN_TRUE, "true==true");
        assertExecution(Value.BOOLEAN_FALSE, "false==true");
        assertExecution(Value.BOOLEAN_FALSE, "true==false");
        assertExecution(Value.BOOLEAN_FALSE, "1==0");
        assertExecution(Value.BOOLEAN_TRUE, "2==2");
        assertExecution(Value.BOOLEAN_TRUE, "2.4==2.4");
        assertExecution(Value.BOOLEAN_TRUE, "\"test\"==\"test\"");
        assertExecution(Value.BOOLEAN_FALSE, "\"test\"==\"test2\"");
        assertExecution(Value.BOOLEAN_TRUE, "1==1.0");
    }

    @Override
    public void testExecution_illegalArgument() throws Exception {
        assertIllegalArgumentException("true==1");
    }

    @Override
    public void testExecution_illegalState() throws Exception {
        assertIllegalStateException("==");
        assertIllegalStateException("1==");
        assertIllegalStateException("==1");
    }
    

    @Override
    public void testKeyword() {
        assertEquals("==",HANDLER.getKeyword());
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
    
}
