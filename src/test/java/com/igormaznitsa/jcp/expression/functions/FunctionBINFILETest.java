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
    assertEquals("(byte)0x48,(byte)0x65,(byte)0x6C,(byte)0x6C,(byte)0x6F,(byte)0x20,(byte)0x50,(byte)0x72" + eof + ",(byte)0x65,(byte)0x70,(byte)0x72,(byte)0x6F,(byte)0x63,(byte)0x65,(byte)0x73,(byte)0x73" + eof + ",(byte)0x6F,(byte)0x72,(byte)0x21", result.asString().trim());
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_UINT8ArrayEncoding() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());
    context.setLocalVariable("hello_world", Value.valueOf("Hello World!"));
    final Value result = Expression.evalExpression("binfile(\"./eval/TestBinLong.txt\",\"uint8[]\")", context);
    assertEquals("208,186,208,176,208,186,208,184,208,181,32,209,130,208,190,32,209,128,209,131,209,129,209,129,208,186,208,184,208,181,32,208,177,209,131,208,186,208,178,209,139,10,72,101,108,108,111,32,80,114,101,112,114,111,99,101,115,115,111,114,33,32,105,116,32,105,115,32,118,101,114,121,32,118,101,114,121,32,118,101,114,121,32,108,111,110,103,32,108,105,110,101,32,116,111,32,102,105,108,108,32,116,104,101,32,102,105,108,101,33,32,115,111,109,101,116,105,109,101,32,105,116,32,105,115,32,117,115,101,102,117,108,32,102,111,114,32,116,101,115,116,115,33,10,97,108,115,32,73,32,100,101,99,105,100,101,100,32,116,111,32,97,100,100,32,111,110,101,32,109,111,114,101,32,108,105,110,101,46", result.asString().trim());
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_INT8ArrayEncoding() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());
    context.setLocalVariable("hello_world", Value.valueOf("Hello World!"));
    final Value result = Expression.evalExpression("binfile(\"./eval/TestBinLong.txt\",\"int8[]\")", context);
    assertEquals("-48,-70,-48,-80,-48,-70,-48,-72,-48,-75,32,-47,-126,-48,-66,32,-47,-128,-47,-125,-47,-127,-47,-127,-48,-70,-48,-72,-48,-75,32,-48,-79,-47,-125,-48,-70,-48,-78,-47,-117,10,72,101,108,108,111,32,80,114,101,112,114,111,99,101,115,115,111,114,33,32,105,116,32,105,115,32,118,101,114,121,32,118,101,114,121,32,118,101,114,121,32,108,111,110,103,32,108,105,110,101,32,116,111,32,102,105,108,108,32,116,104,101,32,102,105,108,101,33,32,115,111,109,101,116,105,109,101,32,105,116,32,105,115,32,117,115,101,102,117,108,32,102,111,114,32,116,101,115,116,115,33,10,97,108,115,32,73,32,100,101,99,105,100,101,100,32,116,111,32,97,100,100,32,111,110,101,32,109,111,114,101,32,108,105,110,101,46", result.asString().trim());
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_UINT8ArrayEncodingSplitted() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());
    context.setLocalVariable("hello_world", Value.valueOf("Hello World!"));
    final Value result = Expression.evalExpression("binfile(\"./eval/TestBinLong.txt\",\"uint8[]s\")", context);
    final String eof = System.getProperty("line.separator");
    assertEquals("208,186,208,176,208,186,208,184,208,181,32,209,130,208,190,32,209,128,209,131,209" + eof
        + ",129,209,129,208,186,208,184,208,181,32,208,177,209,131,208,186,208,178,209,139" + eof
        + ",10,72,101,108,108,111,32,80,114,101,112,114,111,99,101,115,115,111,114,33,32,105" + eof
        + ",116,32,105,115,32,118,101,114,121,32,118,101,114,121,32,118,101,114,121,32,108" + eof
        + ",111,110,103,32,108,105,110,101,32,116,111,32,102,105,108,108,32,116,104,101,32" + eof
        + ",102,105,108,101,33,32,115,111,109,101,116,105,109,101,32,105,116,32,105,115,32" + eof
        + ",117,115,101,102,117,108,32,102,111,114,32,116,101,115,116,115,33,10,97,108,115" + eof
        + ",32,73,32,100,101,99,105,100,101,100,32,116,111,32,97,100,100,32,111,110,101,32" + eof
        + ",109,111,114,101,32,108,105,110,101,46", result.asString().trim());
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_INT8ArrayEncodingSplitted() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());
    context.setLocalVariable("hello_world", Value.valueOf("Hello World!"));
    final Value result = Expression.evalExpression("binfile(\"./eval/TestBinLong.txt\",\"int8[]s\")", context);
    final String eof = System.getProperty("line.separator");
    assertEquals("-48,-70,-48,-80,-48,-70,-48,-72,-48,-75,32,-47,-126,-48,-66,32,-47,-128,-47,-125" + eof
        + ",-47,-127,-47,-127,-48,-70,-48,-72,-48,-75,32,-48,-79,-47,-125,-48,-70,-48,-78,-47" + eof
        + ",-117,10,72,101,108,108,111,32,80,114,101,112,114,111,99,101,115,115,111,114,33" + eof
        + ",32,105,116,32,105,115,32,118,101,114,121,32,118,101,114,121,32,118,101,114,121" + eof
        + ",32,108,111,110,103,32,108,105,110,101,32,116,111,32,102,105,108,108,32,116,104" + eof
        + ",101,32,102,105,108,101,33,32,115,111,109,101,116,105,109,101,32,105,116,32,105" + eof
        + ",115,32,117,115,101,102,117,108,32,102,111,114,32,116,101,115,116,115,33,10,97,108" + eof
        + ",115,32,73,32,100,101,99,105,100,101,100,32,116,111,32,97,100,100,32,111,110,101" + eof
        + ",32,109,111,114,101,32,108,105,110,101,46", result.asString().trim());
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
    assertAllowedArguments(HANDLER, new ValueType[][]{{ValueType.STRING, ValueType.STRING}});
  }

  @Override
  public void testResultType() {
    assertEquals(ValueType.STRING, HANDLER.getResultType());
  }
}
