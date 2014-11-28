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
import com.igormaznitsa.jcp.expression.ValueType;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionABSTest extends AbstractFunctionTest {

  private static final FunctionABS HANDLER = new FunctionABS();

  @Test
  public void testExecution_Int() throws Exception {
    assertFunction("abs(-10)", Value.valueOf(Long.valueOf(10)));
    assertFunction("abs(1-3*2)", Value.valueOf(Long.valueOf(5)));
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_Float() throws Exception {
    assertFunction("abs(-10.5)", Value.valueOf(Float.valueOf(10.5f)));
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_wrongCases() throws Exception {
    assertFunctionException("abs(\"test\")");
    assertFunctionException("abs()");
    assertFunctionException("abs(false)");
    assertFunctionException("abs(1,2,3)");
    assertDestinationFolderEmpty();
  }

  @Override
  public void testName() {
    assertEquals("abs", HANDLER.getName());
  }

  @Override
  public void testReference() {
    assertReference(HANDLER);
  }

  @Override
  public void testArity() {
    assertEquals(1, HANDLER.getArity());
  }

  @Override
  public void testAllowedArgumentTypes() {
    assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.INT}, {ValueType.FLOAT}});
  }

  @Override
  public void testResultType() {
    assertEquals(ValueType.ANY, HANDLER.getResultType());
  }

}
