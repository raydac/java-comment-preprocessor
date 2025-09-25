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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import java.nio.charset.StandardCharsets;

public class OutCharsetHandlerTest extends AbstractCommandLineHandlerTest {

  private static final OutCharsetHandler HANDLER = new OutCharsetHandler();

  @Override
  public void testThatTheHandlerInTheHandlerList() {
    assertHandlerInTheHandlerList(HANDLER);
  }

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext mock = prepareMockContext();

    assertFalse(HANDLER.processCommandLineKey("/o:UUU", mock));
    assertFalse(HANDLER.processCommandLineKey("/TT:", mock));
    assertFalse(HANDLER.processCommandLineKey("/tT", mock));
    assertTrue(HANDLER.processCommandLineKey("/tt:" + StandardCharsets.UTF_16.name(), mock));
    verify(mock).setTargetEncoding(StandardCharsets.UTF_16);

    reset(mock);

    assertTrue(HANDLER.processCommandLineKey("/TT:" + StandardCharsets.UTF_16LE.name(), mock));
    verify(mock).setTargetEncoding(StandardCharsets.UTF_16LE);
  }

  @Override
  public void testName() {
    assertEquals("/TT:", HANDLER.getKeyName());
  }

  @Override
  public void testDescription() {
    assertDescription(HANDLER);
  }
}
