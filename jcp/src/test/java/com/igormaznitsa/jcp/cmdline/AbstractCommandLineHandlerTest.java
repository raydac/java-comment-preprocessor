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

import com.igormaznitsa.jcp.AbstractMockPreprocessorContextTest;
import com.igormaznitsa.jcp.JcpPreprocessor;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class AbstractCommandLineHandlerTest extends AbstractMockPreprocessorContextTest {

  @Test
  public abstract void testExecution() throws Exception;

  @Test
  public abstract void testName();

  @Test
  public abstract void testDescription();

  @Test
  public abstract void testThatTheHandlerInTheHandlerList();

  protected void assertDescription(final CommandLineHandler handler) {
    assertNotNull("Reference must not be null", handler.getDescription());
    assertFalse("Reference must not be empty one", handler.getDescription().isEmpty());
    assertTrue("Reference length must be great than 10 chars", handler.getDescription().length() > 10);
  }

  protected void assertHandlerInTheHandlerList(final CommandLineHandler handler) {
    for (final CommandLineHandler h : JcpPreprocessor.getCommandLineHandlers()) {
      if (handler.getClass() == h.getClass()) {
        return;
      }
    }
    fail("There is not the handler in the common command line handler list");
  }

}
