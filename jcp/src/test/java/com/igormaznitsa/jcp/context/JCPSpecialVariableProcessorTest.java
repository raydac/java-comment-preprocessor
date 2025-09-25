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
import com.igormaznitsa.jcp.InfoHelper;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import java.io.File;
import org.junit.Test;

public class JCPSpecialVariableProcessorTest extends AbstractMockPreprocessorContextTest {

  @Test
  public void testReadVariable() throws Exception {
    final PreprocessorContext context = prepareMockContext();

    assertEquals("Must be equal", InfoHelper.getVersion(),
        new JCPSpecialVariableProcessor().getVariable("jcp.version", context).asString());
    assertNotNull(new JCPSpecialVariableProcessor().getVariable("__line__", context).toString());
    assertNotNull(new JCPSpecialVariableProcessor().getVariable("__date__", context).asString());
    assertNotNull(new JCPSpecialVariableProcessor().getVariable("__time__", context).asString());
    assertNotNull(
        new JCPSpecialVariableProcessor().getVariable("__timestamp__", context).asString());
  }

  @Test(expected = PreprocessorException.class)
  public void testReadUnknownVariable() {
    new JCPSpecialVariableProcessor().getVariable("jcp.version2",
        new PreprocessorContext(new File("some_impossible_folder_121212")));
  }

  @Test(expected = PreprocessorException.class)
  public void testWriteDisallowed() {
    new JCPSpecialVariableProcessor().setVariable("jcp.version", Value.INT_ONE,
        new PreprocessorContext(new File("some_impossible_folder_121212")));
  }
}
