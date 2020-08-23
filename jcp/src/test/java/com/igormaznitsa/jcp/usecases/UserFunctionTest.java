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

package com.igormaznitsa.jcp.usecases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


import com.igormaznitsa.jcp.JcpPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;

public class UserFunctionTest extends AbstractUseCaseTest implements PreprocessorExtension {

  int calledfunc;
  int calledaction;

  @Override
  protected void tuneContext(PreprocessorContext context) {
    context.setPreprocessorExtension(this);
  }

  @Override
  public void check(PreprocessorContext context, JcpPreprocessor.Statistics stat) throws Exception {
    assertEquals("User function must be called once", 1, calledfunc);
    assertEquals("User action must be called twice", 2, calledaction);
    assertEquals(0, stat.getCopied());
    assertEquals(1, stat.getPreprocessed());
  }

  @Override
  public boolean processAction(final PreprocessorContext context, final Value[] parameters) {
    calledaction++;
    assertEquals(1000L, parameters[0].asLong().longValue());
    assertEquals("hello", parameters[1].asString());
    assertEquals(123L, parameters[2].asLong().longValue());
    return true;
  }

  @Override
  public Value processUserFunction(final String functionName, final Value[] arguments) {
    if ("testfunc".equals(functionName) && arguments.length == 3) {
      calledfunc++;
      assertEquals(1L, arguments[0].asLong().longValue());
      assertEquals("hry", arguments[1].asString());
      assertEquals(3L, arguments[2].asLong().longValue());
      return Value.valueOf("yayaya");
    } else {
      fail("Unexpected function '" + functionName + '\'');
      throw new RuntimeException("Error");
    }
  }

  @Override
  public int getUserFunctionArity(final String functionName) {
    return "testfunc".equals(functionName) ? 3 : -1;
  }

}
