/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    assertEquals(testResult, Expression.evalExpression("$test(1,2,3,4,5+6)", context));

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

    assertEquals(testResult, Expression.evalExpression("$test()", context));

    verify(mock).processUserFunction(eq("test"), AdditionalMatchers.aryEq(new Value[0]));
  }
}
