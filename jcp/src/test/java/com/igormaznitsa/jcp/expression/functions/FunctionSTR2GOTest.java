/*
 * Copyright 2002-2019 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.igormaznitsa.jcp.expression.functions;

import static org.junit.Assert.assertEquals;

import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.util.List;
import org.junit.Test;

public class FunctionSTR2GOTest extends AbstractFunctionTest {

  private static final FunctionSTR2GO HANDLER = new FunctionSTR2GO();

  @Test
  public void testExecution_NoSplit() throws Exception {
    assertFunction("str2go(\"\",false)", Value.valueOf(""));
    assertFunction("str2go(\"hello\nworld\",false)", Value.valueOf("hello\\nworld"));
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_Split() throws Exception {
    assertFunction("str2go(\"\",true)", Value.valueOf("\"\""));
    assertFunction("str2go(\"hello\nworld\",true)",
        Value.valueOf("\"hello\\n\"" + PreprocessorUtils.getNextLineCodes() + "+\"world\""));
    assertFunction("str2go(\"hello\nworld\n\",true)",
        Value.valueOf("\"hello\\n\"" + PreprocessorUtils.getNextLineCodes() + "+\"world\\n\""));
    assertFunction("str2go(\"\u000bhello\u0007\nworld\n\",true)", Value.valueOf(
        "\"\\vhello\\a\\n\"" + PreprocessorUtils.getNextLineCodes() + "+\"world\\n\""));
    assertFunction("str2go(\"Здравствуй\nМир\n\",true)", Value.valueOf(
        "\"\\u0417\\u0434\\u0440\\u0430\\u0432\\u0441\\u0442\\u0432\\u0443\\u0439\\n\"" +
            PreprocessorUtils.getNextLineCodes() + "+\"\\u041c\\u0438\\u0440\\n\""));
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_wrongCases() throws Exception {
    assertFunctionException("str2go()");
    assertFunctionException("str2go(1,2)");
    assertFunctionException("str2go(true)");
    assertFunctionException("str2go(true,\"ss\")");
    assertFunctionException("str2go(\"ss\",3)");
    assertDestinationFolderEmpty();

  }

  @Override
  public void testName() {
    assertEquals("str2go", HANDLER.getName());
  }

  @Override
  public void testReference() {
    assertReference(HANDLER);
  }

  @Override
  public void testArity() {
    assertEquals(AbstractFunction.ARITY_2, HANDLER.getArity());
  }

  @Override
  public void testAllowedArgumentTypes() {
    assertAllowedArguments(HANDLER, List.of(List.of(ValueType.STRING, ValueType.BOOLEAN)));
  }

  @Override
  public void testResultType() {
    assertEquals(ValueType.STRING, HANDLER.getResultType());
  }
}
