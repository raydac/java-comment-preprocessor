package com.igormaznitsa.jcp.expression.functions;

import com.igormaznitsa.jcp.expression.Value;
import static org.mockito.Mockito.*;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import static org.junit.Assert.*;

public class FunctionDefinedByUserTest {

    @Test
    public void testExecution_withArguments() {
        final PreprocessorExtension mock = mock(PreprocessorExtension.class);
        
        final PreprocessorContext context = new PreprocessorContext();
        final Value testResult = Value.valueOf("result");
        context.setPreprocessorExtension(mock);
        
        when(mock.processUserFunction(eq("test"), any(Value[].class))).thenReturn(testResult);
        when(mock.getUserFunctionArity(eq("test"))).thenReturn(Integer.valueOf(5));
        
        assertEquals(testResult,Expression.evalExpression("$test(1,2,3,4,5+6)", context));
        
        verify(mock).processUserFunction(eq("test"), AdditionalMatchers.aryEq(new Value[]{
            Value.valueOf(Long.valueOf(1L)),
            Value.valueOf(Long.valueOf(2L)),
            Value.valueOf(Long.valueOf(3L)),
            Value.valueOf(Long.valueOf(4L)),
            Value.valueOf(Long.valueOf(11L))}));
    }

    @Test
    public void testExecution_withoutArguments() {
        final PreprocessorExtension mock = mock(PreprocessorExtension.class);
        
        final PreprocessorContext context = new PreprocessorContext();
        final Value testResult = Value.valueOf("result");
        context.setPreprocessorExtension(mock);
        
        when(mock.processUserFunction(eq("test"), any(Value[].class))).thenReturn(testResult);
        when(mock.getUserFunctionArity(eq("test"))).thenReturn(Integer.valueOf(0));
        
        assertEquals(testResult,Expression.evalExpression("$test()", context));
        
        verify(mock).processUserFunction(eq("test"), AdditionalMatchers.aryEq(new Value[0]));
    }
}
