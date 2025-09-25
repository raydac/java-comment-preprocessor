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

package com.igormaznitsa.jcp.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.igormaznitsa.jcp.AbstractMockPreprocessorContextTest;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import org.junit.Test;

public class EnvironmentVariableProcessorTest extends AbstractMockPreprocessorContextTest {

  @Test
  public void testReadVariable() throws Exception {
    final PreprocessorContext context = prepareMockContext();

    final String javaVersion = System.getProperty("java.version");
    final String osName = System.getProperty("os.name");

    assertNotNull("Must not be null", javaVersion);
    assertNotNull("Must not be null", osName);

    final EnvironmentVariableProcessor test = new EnvironmentVariableProcessor();

    assertEquals("Must be equal", javaVersion,
        test.getVariable("env.java.version", context).asString());
    assertEquals("Must be equal", osName, test.getVariable("env.os.name", context).asString());
  }

  @Test(expected = PreprocessorException.class)
  public void testReadUnknownVariable() throws Exception {
    new EnvironmentVariableProcessor().getVariable("kjhaksjdhksajqwoiueoqiwue",
        prepareMockContext());
  }

  @Test(expected = PreprocessorException.class)
  public void testWriteVariable() throws Exception {
    PreprocessorContext context = prepareMockContext();
    new EnvironmentVariableProcessor().setVariable("kjhaksjdhksajqwoiueoqiwue", Value.BOOLEAN_FALSE,
        context);
  }
}
