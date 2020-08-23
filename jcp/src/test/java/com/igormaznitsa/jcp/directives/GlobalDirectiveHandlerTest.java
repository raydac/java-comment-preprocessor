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

package com.igormaznitsa.jcp.directives;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import org.junit.Test;

public class GlobalDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {

  private static final GlobalDirectiveHandler HANDLER = new GlobalDirectiveHandler();

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext context = executeGlobalPhase("directive_global.txt", null);
    assertTrue(context.containsGlobalVariable("xxx"));
    final Value var = context.findVariableForName("xxx", true);
    assertEquals(Long.valueOf(10), var.asLong());
  }

  @Test
  public void testExecution_PreprocessingPhase() throws Exception {
    final PreprocessorContext context = assertFilePreprocessing("directive_global.txt", false, null, null);
    assertFalse(context.containsGlobalVariable("xxx"));
    assertNull(context.findVariableForName("xxx", true));
  }

  @Test
  public void testExecution_WrongCases() throws Exception {
    assertGlobalPhaseException("\n\n//#global 23123", 3, null);
    assertGlobalPhaseException("\n\n//#global", 3, null);
    assertGlobalPhaseException("\n\n//#global ", 3, null);
    assertGlobalPhaseException("\n\n//#global hh=", 3, null);
    assertGlobalPhaseException("\n\n//#global xx==10", 3, null);
    assertGlobalPhaseException("\n\n//#global =10", 3, null);
    assertGlobalPhaseException("\n\n//#global ====", 3, null);
  }

  @Override
  public void testExecutionCondition() throws Exception {
    assertTrue(HANDLER.executeOnlyWhenExecutionAllowed());
  }

  @Override
  public void testKeyword() throws Exception {
    assertEquals("global", HANDLER.getName());
  }

  @Override
  public void testPhase() throws Exception {
    assertTrue(HANDLER.isGlobalPhaseAllowed());
    assertFalse(HANDLER.isPreprocessingPhaseAllowed());
  }

  @Override
  public void testReference() throws Exception {
    assertReference(HANDLER);
  }

  @Override
  public void testArgumentType() throws Exception {
    assertEquals(DirectiveArgumentType.SET, HANDLER.getArgumentType());
  }
}
