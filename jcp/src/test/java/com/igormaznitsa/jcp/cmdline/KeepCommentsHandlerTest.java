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

package com.igormaznitsa.jcp.cmdline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.igormaznitsa.jcp.context.KeepComments;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;

public class KeepCommentsHandlerTest extends AbstractCommandLineHandlerTest {

  private static final KeepCommentsHandler HANDLER = new KeepCommentsHandler();

  @Override
  public void testThatTheHandlerInTheHandlerList() {
    assertHandlerInTheHandlerList(HANDLER);
  }

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext mock = prepareMockContext();

    assertFalse(HANDLER.processCommandLineKey("", mock));
    assertFalse(HANDLER.processCommandLineKey("/m", mock));
    assertFalse(HANDLER.processCommandLineKey("/mm:", mock));
    assertThrows(PreprocessorException.class, () -> HANDLER.processCommandLineKey("/m:", mock));

    assertEquals(KeepComments.REMOVE_ALL,mock.getKeepComments());
    assertTrue(HANDLER.processCommandLineKey("/m:keep_all", mock));
    assertEquals(KeepComments.KEEP_ALL,mock.getKeepComments());
  }

  @Override
  public void testName() {
    assertEquals("/M:", HANDLER.getKeyName());
  }

  @Override
  public void testDescription() {
    assertDescription(HANDLER);
  }

}
