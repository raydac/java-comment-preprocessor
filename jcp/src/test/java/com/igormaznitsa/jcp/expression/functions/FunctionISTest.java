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
import org.junit.Test;

public class FunctionISTest extends AbstractFunctionTest {

  private static final FunctionIS HANDLER = new FunctionIS();

  @Test
  public void testExecution_StrAny_VarPresented() throws Exception {
    assertFunction("is(\"hello_var\",true)", Value.BOOLEAN_TRUE, var("hello_var", Value.BOOLEAN_TRUE), null);
    assertFunction("is(\"HELLO_VAR\",true)", Value.BOOLEAN_TRUE, var("hello_var", Value.BOOLEAN_TRUE), null);
    assertFunction("is(\"hello_var\",false)", Value.BOOLEAN_FALSE, var("hello_var", Value.BOOLEAN_TRUE), null);
    assertFunction("is(\"hello_var\",true)", Value.BOOLEAN_TRUE, null, var("hello_var", Value.BOOLEAN_TRUE));
    assertFunction("is(\"hello_var\",false)", Value.BOOLEAN_FALSE, null, var("hello_var", Value.BOOLEAN_TRUE));
    assertFunction("is(\"hello_var\",\"true\")", Value.BOOLEAN_TRUE, null, var("hello_var", Value.BOOLEAN_TRUE));
    assertFunction("is(\"hello_var\",\"false\")", Value.BOOLEAN_FALSE, null, var("hello_var", Value.BOOLEAN_TRUE));
    assertFunction("is(\"hello_var\",\"1\")", Value.BOOLEAN_TRUE, null, var("hello_var", Value.INT_ONE));
    assertFunction("is(\"hello_var\",\"2\")", Value.BOOLEAN_FALSE, null, var("hello_var", Value.INT_ONE));
    assertFunction("is(\"hello_var\",1)", Value.BOOLEAN_TRUE, null, var("hello_var", Value.valueOf("1")));
    assertFunction("is(\"hello_var\",2)", Value.BOOLEAN_FALSE, null, var("hello_var", Value.valueOf("1")));
    assertDestinationFolderEmpty();
  }

  @Test
  public void testExecution_StrAny_VarNotPresented() throws Exception {
    assertFunction("is(\"hello_var\",true)", Value.BOOLEAN_FALSE);
    assertFunction("is(\"hello_var\",false)", Value.BOOLEAN_FALSE);
    assertFunction("is(\"hello_var\",true)", Value.BOOLEAN_FALSE);
    assertFunction("is(\"hello_var\",false)", Value.BOOLEAN_FALSE);
    assertFunction("is(\"hello_var\",\"true\")", Value.BOOLEAN_FALSE);
    assertFunction("is(\"hello_var\",\"false\")", Value.BOOLEAN_FALSE);
    assertFunction("is(\"hello_var\",\"1\")", Value.BOOLEAN_FALSE);
    assertFunction("is(\"hello_var\",\"2\")", Value.BOOLEAN_FALSE);
    assertFunction("is(\"hello_var\",1)", Value.BOOLEAN_FALSE);
    assertFunction("is(\"hello_var\",2)", Value.BOOLEAN_FALSE);
    assertDestinationFolderEmpty();
  }
//
//  @Test
//  public void testExecution_StrStr_wrongCases() throws Exception {
//    assertFunctionException("issubstr()");
//    assertFunctionException("issubstr(\"test\")");
//    assertFunctionException("issubstr(,)");
//    assertFunctionException("issubstr(1,\"ttt\")");
//    assertFunctionException("issubstr(false,\"ttt\")");
//    assertFunctionException("issubstr(false,true)");
//    assertFunctionException("issubstr(\"d\",1)");
//    assertDestinationFolderEmpty();
//  }

  @Override
  public void testName() {
    assertEquals("is", HANDLER.getName());
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
    assertAllowedArguments(HANDLER, new ValueType[][] {{ValueType.STRING, ValueType.ANY}});
  }

  @Override
  public void testResultType() {
    assertEquals(ValueType.BOOLEAN, HANDLER.getResultType());
  }
}
