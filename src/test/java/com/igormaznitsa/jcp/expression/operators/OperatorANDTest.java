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
  public void testExecution_PreprocessorException() throws Exception {
    assertPreprocessorException("&&");
    assertPreprocessorException("true &&");
    assertPreprocessorException("&& false");
    assertPreprocessorException("\"test\" && true");
    assertPreprocessorException("false && 1.3");
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
    assertEquals(ExpressionItemPriority.LOGICAL, HANDLER.getExpressionItemPriority());
  }

}
