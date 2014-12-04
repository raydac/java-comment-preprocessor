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

import com.igormaznitsa.jcp.expression.ExpressionItemPriority;
import com.igormaznitsa.jcp.expression.Value;
import static org.junit.Assert.*;

public class OperatorSUBTest extends AbstractOperatorTest {

  private static final OperatorSUB HANDLER = new OperatorSUB();

  @Override
  public void testKeyword() {
    assertEquals("-", HANDLER.getKeyword());
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
    assertEquals(ExpressionItemPriority.ARITHMETIC_ADD_SUB, HANDLER.getExpressionItemPriority());
  }

  @Override
  public void testExecution() throws Exception {
    assertExecution(Value.INT_ZERO, "10-10");
    assertExecution(Value.INT_THREE, "7-4");
    assertExecution(Value.valueOf(Float.valueOf(1.5f - 1.2f)), "1.5-1.2");
    assertExecution(Value.valueOf(Float.valueOf(1.0f - 1.2f)), "1-1.2");
    assertExecution(Value.valueOf(Float.valueOf(-1.2f)), "-1.2");
  }

  @Override
  public void testExecution_PreprocessorException() throws Exception {
    assertPreprocessorException("-");
//TODO        assertIllegalStateException("1-");
    assertPreprocessorException("1-\"test\"");
    assertPreprocessorException("1-true");
    assertPreprocessorException("true-1");
    assertPreprocessorException("true-1.1");
    assertPreprocessorException("true-false");
  }

}
