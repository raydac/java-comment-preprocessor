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

import com.igormaznitsa.jcp.context.PreprocessingState.ExcludeIfInfo;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExcludeIfDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {

  private static final ExcludeIfDirectiveHandler HANDLER = new ExcludeIfDirectiveHandler();

  @Override
  public void testExecution() throws Exception {
    final List<ExcludeIfInfo> list = new ArrayList<>();
    executeGlobalPhase("directive_excludeif.txt", list);
    assertEquals("Must be two //#excludeif ", list.size(), 2);
    final ExcludeIfInfo info1 = list.get(1);
    final ExcludeIfInfo info2 = list.get(0);

    assertEquals("true", info1.getCondition());
    assertEquals(2, info1.getStringIndex());
    assertNotNull(info1.getFileInfoContainer());

    assertEquals("hello+world", info2.getCondition());
    assertEquals(6, info2.getStringIndex());
    assertNotNull(info2.getFileInfoContainer());
  }

  @Override
  public void testKeyword() throws Exception {
    assertEquals("excludeif", HANDLER.getName());
  }

  @Override
  public void testExecutionCondition() throws Exception {
    assertTrue(HANDLER.executeOnlyWhenExecutionAllowed());
  }

  @Override
  public void testReference() throws Exception {
    assertReference(HANDLER);
  }

  @Override
  public void testPhase() throws Exception {
    assertTrue(HANDLER.isGlobalPhaseAllowed());
    assertFalse(HANDLER.isPreprocessingPhaseAllowed());
  }

  @Override
  public void testArgumentType() throws Exception {
    assertEquals(DirectiveArgumentType.BOOLEAN, HANDLER.getArgumentType());
  }
}
