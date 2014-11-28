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
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionSTR2JAVATest extends AbstractFunctionTest {

  private static final FunctionSTR2JAVA HANDLER = new FunctionSTR2JAVA();

  @Test
  public void testExecution_NoSplit() throws Exception {
    assertFunction("str2java(\"\",false)", Value.valueOf(""));
    assertFunction("str2java(\"hello\nworld\",false)", Value.valueOf("hello\\nworld"));
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_Split() throws Exception {
    assertFunction("str2java(\"\",true)", Value.valueOf("\"\""));
    assertFunction("str2java(\"hello\nworld\",true)", Value.valueOf("\"hello\\n\""+PreprocessorUtils.getNextLineCodes()+"+\"world\""));
    assertFunction("str2java(\"hello\nworld\n\",true)", Value.valueOf("\"hello\\n\""+PreprocessorUtils.getNextLineCodes()+"+\"world\\n\""));
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_wrongCases() throws Exception {
    assertFunctionException("str2web()");
    assertFunctionException("str2web(1,2)");
    assertFunctionException("str2web(true)");
    assertFunctionException("str2web(true,\"ss\")");
    assertFunctionException("str2web(\"ss\",3)");
    assertDestinationFolderEmpty();
   
  }

  @Override
  public void testName() {
    assertEquals("str2java", HANDLER.getName());
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
  public void testAllowedArgumentTypes() {
    assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.STRING, ValueType.BOOLEAN}});
  }

  @Override
  public void testResultType() {
    assertEquals(ValueType.STRING, HANDLER.getResultType());
  }
}
