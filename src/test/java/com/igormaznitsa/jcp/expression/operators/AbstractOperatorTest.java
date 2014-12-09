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
package com.igormaznitsa.jcp.expression.operators;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;
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
  public abstract void testExecution_PreprocessorException() throws Exception;

  public void assertReference(final AbstractOperator operator) {
    final String reference = operator.getReference();
    assertNotNull("The reference must not be null", reference);
    assertFalse("The reference must not be empty", reference.isEmpty());
    assertTrue("The reference must be longer that 7 chars", reference.length() > 7);
  }

  public PreprocessorContext assertExecution(final Value expectedResult, final String expression) throws Exception {
    final PreprocessorContext context = new PreprocessorContext();
    assertEquals("The expression result must be equals to the expected one", expectedResult, Expression.evalExpression(expression, context));
    return context;
  }

  public void assertPreprocessorException(final String expression) {
    try {
      assertExecution(Value.INT_ZERO, expression);
      fail("Must throw PE");
    }
    catch (PreprocessorException expected) {
    }
    catch (Exception unexpected) {
      unexpected.printStackTrace();
      fail("Unexpected exception detected, must be you have a program error");
    }
  }
}
