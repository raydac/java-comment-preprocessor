package com.igormaznitsa.jcp.expression.functions;

import org.junit.Test;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import java.io.IOException;
import java.util.Arrays;
import static org.junit.Assert.*;

public abstract class AbstractFunctionTest {

    @Test
    public abstract void testName();

    @Test
    public abstract void testReference();

    @Test
    public abstract void testArity();

    @Test
    public abstract void testAllowedArgumentTypes();

    @Test
    public abstract void testResultType();

    protected void assertReference(final AbstractFunction function) {
        final String reference = function.getReference();
        assertNotNull("Reference must not be null", reference);
        assertFalse("Reference must not be empty", reference.isEmpty());
        assertTrue("Reference must not be too short", reference.length() > 10);
    }

    protected void assertAllowedArguments(final AbstractFunction function, final ValueType[][] checkingData) {
        final ValueType[][] argTypes = function.getAllowedArgumentTypes();
        for (final ValueType[] currentTypes : argTypes) {
            boolean found = false;
            for (final ValueType[] etalon : checkingData) {
                if (Arrays.deepEquals(currentTypes, etalon)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                continue;
            } else {
                fail("Found not allowed argument types " + Arrays.toString(currentTypes));
            }
        }
    }

    protected void assertFunction(final String expression, final Value expected) throws IOException {
        final PreprocessorContext context = new PreprocessorContext();
        final Value result = Expression.evalExpression(expression, context);
        assertEquals("Must be equals", expected, result);
    }

    protected void assertFunctionException(final String expression) throws IOException {
        final PreprocessorContext context = new PreprocessorContext();
        try {
            Expression.evalExpression(expression, context);
            fail("Must throw RuntimeException [" + expression + ']');
        } catch (RuntimeException ex) {
        }
    }
}
