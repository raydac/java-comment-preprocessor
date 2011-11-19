package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import org.junit.Test;
import static org.junit.Assert.*;

public class OperatorANDTest extends AbstractOperatorTest {

    private static final OperatorAND HANDLER = new OperatorAND();

    @Test
    public void testExecution() throws Exception {
        assertExecution(Value.valueOf(Long.valueOf(1L)), "3 && 1");
        assertExecution(Value.valueOf(Long.valueOf(0L)), "1 && 0");
        assertExecution(Value.valueOf(Long.valueOf(1L)), "1 && 3");

        assertExecution(Value.valueOf(Boolean.TRUE), "true && true");
        assertExecution(Value.valueOf(Boolean.FALSE), "false && true");
        assertExecution(Value.valueOf(Boolean.FALSE), "true && false");
        assertExecution(Value.valueOf(Boolean.FALSE), "false && false");
        assertExecution(Value.valueOf(Boolean.FALSE), "false && false && true");
        assertExecution(Value.valueOf(Boolean.TRUE), "true && true && true");
    }
    
    @Override
    public void testExecution_illegalState() throws Exception {
        assertIllegalStateException("&&");
        assertIllegalStateException("true &&");
        assertIllegalStateException("&& false");
    }
    
    @Override
    public void testExecution_illegalArgument() throws Exception {
        assertIllegalArgumentException("\"test\" && true");
        assertIllegalArgumentException("false && 1.3");
    }
    
    @Override
    public void testKeyword() {
        assertEquals("&&", HANDLER.getKeyword());
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
    
}
