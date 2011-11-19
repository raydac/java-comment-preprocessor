package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class AbstractOperatorTest {
    @Test
    public abstract void testKeyword();
    @Test
    public abstract void testReference();
    @Test
    public abstract void testArity();
    @Test
    public abstract void testPriority();
    @Test
    public abstract void testExecution() throws Exception;
    @Test
    public abstract void testExecution_illegalState() throws Exception;
    @Test
    public abstract void testExecution_illegalArgument() throws Exception;
    
    public void assertReference(final AbstractOperator operator) {
        final String reference = operator.getReference();
        assertNotNull("The reference must not be null",reference);
        assertFalse("The reference must not be empty",reference.isEmpty());
        assertTrue("The reference must be longer that 10 chars",reference.length()>10);
    }
    
    public PreprocessorContext assertExecution(final Value expectedResult, final String expression) throws Exception {
        final PreprocessorContext context = new PreprocessorContext();
        assertEquals("The expression result must be equals to the expected one", expectedResult, Expression.evalExpression(expression, context, null));
        return context;
    }
    
    public void assertIllegalStateException(final String expression) {
        try {
            assertExecution(Value.INT_ZERO, expression);
            fail("Must throw ITE");
        }catch(IllegalStateException expected) {
        } catch (Exception unexpected) {
            unexpected.printStackTrace();
            fail("Unexpected exception detected, must be you have a program error");
        }
    }

    public void assertIllegalArgumentException(final String expression) {
        try {
            assertExecution(Value.INT_ZERO, expression);
            fail("Must throw ITE");
        }catch(IllegalArgumentException expected) {
        } catch (Exception unexpected) {
            unexpected.printStackTrace();
            fail("Unexpected exception detected, must be you have a program error");
        }
    }
}
