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

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class FunctionBINFILETest extends AbstractFunctionTest {

  private static final FunctionEVALFILE HANDLER = new FunctionEVALFILE();

  @Test
  public void testExecution_Base64Encoding() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());
    context.setLocalVariable("hello_world", Value.valueOf("Hello World!"));
    final Value result = Expression.evalExpression("binfile(\"./eval/TestBin.txt\",\"base64\")", context);
    assertEquals("SGVsbG8gUHJlcHJvY2Vzc29yIQ==", result.asString().trim());
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_Base64EncodingSplitted() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());
    context.setLocalVariable("hello_world", Value.valueOf("Hello World!"));
    final Value result = Expression.evalExpression("binfile(\"./eval/TestBin.txt\",\"base64s\")", context);
    assertEquals("SGVsbG8gUHJlcHJvY2Vzc29yIQ==", result.asString().trim());
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_ByteArrayEncoding() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());
    context.setLocalVariable("hello_world", Value.valueOf("Hello World!"));
    final Value result = Expression.evalExpression("binfile(\"./eval/TestBin.txt\",\"byte[]\")", context);
    assertEquals("(byte)0x48,(byte)0x65,(byte)0x6C,(byte)0x6C,(byte)0x6F,(byte)0x20,(byte)0x50,(byte)0x72,(byte)0x65,(byte)0x70,(byte)0x72,(byte)0x6F,(byte)0x63,(byte)0x65,(byte)0x73,(byte)0x73,(byte)0x6F,(byte)0x72,(byte)0x21", result.asString().trim());
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_ByteArrayEncodingSplitted() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());
    context.setLocalVariable("hello_world", Value.valueOf("Hello World!"));
    final Value result = Expression.evalExpression("binfile(\"./eval/TestBin.txt\",\"byte[]s\")", context);
    final String eof = System.getProperty("line.separator");
    assertEquals("(byte)0x48,(byte)0x65,(byte)0x6C,(byte)0x6C,(byte)0x6F,(byte)0x20,(byte)0x50,(byte)0x72"+eof+",(byte)0x65,(byte)0x70,(byte)0x72,(byte)0x6F,(byte)0x63,(byte)0x65,(byte)0x73,(byte)0x73"+eof+",(byte)0x6F,(byte)0x72,(byte)0x21", result.asString().trim());
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_Str_wrongCases() throws Exception {
    assertFunctionException("binfile()");
    assertFunctionException("binfile(true)");
    assertFunctionException("binfile(,)");
    assertFunctionException("binfile(1,\"ttt\")");
    assertFunctionException("binfile(\"d\",\"ttt\",1)");
    assertFunctionException("binfile(123)");
    assertDestinationFolderEmpty();
  }

  @Override
  public void testName() {
    assertEquals("binfile", HANDLER.getName());
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
    assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.STRING,ValueType.STRING}});
  }

  @Override
  public void testResultType() {
    assertEquals(ValueType.STRING, HANDLER.getResultType());
  }
}
