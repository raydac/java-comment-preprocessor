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

public class FunctionSTR2WEBTest extends AbstractFunctionTest {

  private static final FunctionSTR2WEB HANDLER = new FunctionSTR2WEB();

  @Test
  public void testExecution_Str() throws Exception {
    assertFunction("str2web(\"<hello>\")", Value.valueOf("&lt;hello&gt;"));
    assertFunction("str2web(\"<привет>\")", Value.valueOf("&lt;&#1087;&#1088;&#1080;&#1074;&#1077;&#1090;&gt;"));
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_wrongCases() throws Exception {
    assertFunctionException("str2web()");
    assertFunctionException("str2web(1,2)");
    assertFunctionException("str2web(true)");
    assertFunctionException("str2web(3)");
    assertDestinationFolderEmpty();
   
  }

  @Override
  public void testName() {
    assertEquals("str2web", HANDLER.getName());
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
    assertEquals(ValueType.STRING, HANDLER.getResultType());
  }
}
