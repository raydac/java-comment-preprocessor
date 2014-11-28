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

public class FunctionROUNDTest extends AbstractFunctionTest {

  private static final FunctionROUND HANDLER = new FunctionROUND();

  @Test
  public void testExecution_Float() throws Exception {
    assertFunction("round(4.7)", Value.valueOf(Long.valueOf(5L)));
    assertFunction("round(3.1+3.6)", Value.valueOf(Long.valueOf(7L)));
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_Int() throws Exception {
    assertFunction("round(4)", Value.valueOf(Long.valueOf(4L)));
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_wrongCases() throws Exception {
    assertFunctionException("round(true)");
    assertFunctionException("round(\"aaa\")");
    assertFunctionException("round()");
    assertFunctionException("round(0.3,2.1)");
    assertDestinationFolderEmpty();
  }

  @Override
  public void testName() {
    assertEquals("round", HANDLER.getName());
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
    assertEquals(ValueType.INT, HANDLER.getResultType());
  }

}
