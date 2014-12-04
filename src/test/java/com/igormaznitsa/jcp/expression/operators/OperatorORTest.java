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

public class OperatorORTest extends AbstractOperatorTest {

  private static final OperatorOR HANDLER = new OperatorOR();

  @Override
  public void testKeyword() {
    assertEquals("||", HANDLER.getKeyword());
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
    assertEquals(ExpressionItemPriority.LOGICAL, HANDLER.getExpressionItemPriority());
  }

  @Override
  public void testExecution() throws Exception {
    assertExecution(Value.BOOLEAN_FALSE, "false||false");
    assertExecution(Value.BOOLEAN_TRUE, "true||false");
    assertExecution(Value.BOOLEAN_TRUE, "false||true");
    assertExecution(Value.BOOLEAN_TRUE, "true||true");
    assertExecution(Value.valueOf(Long.valueOf(3L)), "1||2");
  }

  @Override
  public void testExecution_PreprocessorException() throws Exception {
    assertPreprocessorException("||");
    assertPreprocessorException("true||");
    assertPreprocessorException("\"test\"||true");
    assertPreprocessorException("true||1");
    assertPreprocessorException("1.2||1.1");
    assertPreprocessorException("||false");
  }
}
