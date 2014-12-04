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
import org.junit.Test;
import com.igormaznitsa.jcp.expression.Value;
import static org.junit.Assert.*;

public class OperatorADDTest extends AbstractOperatorTest {

  private static final OperatorADD HANDLER = new OperatorADD();

  @Override
  public void testExecution() throws Exception {
    assertExecution(Value.valueOf(Long.valueOf(11)), "3+8");
    assertExecution(Value.valueOf("helloworld"), "\"hello\"+\"world\"");
    assertExecution(Value.valueOf(Float.valueOf(2.2f)), "1.2+1");
    assertExecution(Value.valueOf("1test"), "1+\"test\"");
    assertExecution(Value.valueOf("1.2test"), "1.2+\"test\"");
  }

  @Test
  public void testExecution_chain() throws Exception {
    assertExecution(Value.valueOf("1.2.3"), "1+\".\"+2+\".\"+3");
  }

  @Override
  public void testExecution_PreprocessorException() throws Exception {
    assertPreprocessorException("+");
    assertPreprocessorException("+1");
    assertPreprocessorException("2+");
    assertPreprocessorException("true+false");
    assertPreprocessorException("1+true");
    assertPreprocessorException("2.3+false");
  }

  @Override
  public void testArity() {
    assertEquals(2, HANDLER.getArity());
  }

  @Override
  public void testKeyword() {
    assertEquals("+", HANDLER.getKeyword());
  }

  @Override
  public void testReference() {
    assertReference(HANDLER);
  }

  @Override
  public void testPriority() {
    assertEquals(ExpressionItemPriority.ARITHMETIC_ADD_SUB, HANDLER.getExpressionItemPriority());
  }
}
