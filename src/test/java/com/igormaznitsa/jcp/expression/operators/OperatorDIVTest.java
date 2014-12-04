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
import org.junit.Test;
import static org.junit.Assert.*;

public class OperatorDIVTest extends AbstractOperatorTest {

  private static final OperatorDIV HANDLER = new OperatorDIV();

  @Override
  public void testExecution() throws Exception {
    assertExecution(Value.valueOf(Long.valueOf(5L)), "10/2");
    assertExecution(Value.valueOf(Float.valueOf(1.5f)), "3.0/2");
    assertExecution(Value.valueOf(Long.valueOf(1L)), "3/2");
  }

  @Test
  public void testExecution_chain() throws Exception {
    assertExecution(Value.valueOf(8L), "160/4/5");
  }

  @Override
  public void testExecution_PreprocessorException() {
    assertPreprocessorException("/");
    assertPreprocessorException("1/");
    assertPreprocessorException("/2.2");
    assertPreprocessorException("1/true");
    assertPreprocessorException("false/3");
    assertPreprocessorException("\"hello\"/2.2");
    assertPreprocessorException("1/\"hello\"");
  }

  @Test(expected = ArithmeticException.class)
  public void testExecution_arithmeticException() throws Exception {
    assertExecution(Value.INT_ZERO, "1/0");
  }

  @Override
  public void testKeyword() {
    assertEquals("/", HANDLER.getKeyword());
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
    assertEquals(ExpressionItemPriority.ARITHMETIC_MUL_DIV_MOD, HANDLER.getExpressionItemPriority());
  }
}
