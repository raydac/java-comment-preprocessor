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

public class FunctionSTR2INTTest extends AbstractFunctionTest {

  private static final FunctionSTR2INT HANDLER = new FunctionSTR2INT();

  @Test
  public void testExecute_Str() throws Exception {
    assertFunction("str2int(\"100\")", Value.valueOf(Long.valueOf(100L)));
    assertFunction("str2int(\"0\")", Value.INT_ZERO);
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecute_wrongCase() throws Exception {
    assertFunctionException("str2int(true)");
    assertFunctionException("str2int(0.3)");
    assertFunctionException("str2int(1,2)");
    assertDestinationFolderEmpty();
  }

  @Override
  public void testName() {
    assertEquals("str2int", HANDLER.getName());
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
    assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.STRING}});
  }

  @Override
  public void testResultType() {
    assertEquals(ValueType.INT, HANDLER.getResultType());
  }

}
