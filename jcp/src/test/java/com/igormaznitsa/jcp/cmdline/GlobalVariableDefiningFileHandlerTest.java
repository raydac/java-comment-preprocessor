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


import com.igormaznitsa.jcp.TestUtils;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.File;
import java.util.List;
import org.junit.Test;

public final class GlobalVariableDefiningFileHandlerTest extends AbstractCommandLineHandlerTest {

  private static final GlobalVariableDefiningFileHandler HANDLER = new GlobalVariableDefiningFileHandler();

  @Override
  public void testThatTheHandlerInTheHandlerList() {
    assertHandlerInTheHandlerList(HANDLER);
  }

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext context = new PreprocessorContext(new File("some_impossible_folder_121212"));

    final File testFile = new File(this.getClass().getResource("./global_variable_def.txt").toURI());

    final String param = "@" + PreprocessorUtils.getFilePath(testFile);

    HANDLER.processCommandLineKey(param, context);

    final List<File> configFiles = context.getConfigFiles();

    assertEquals("File must be added", 1, configFiles.size());
    TestUtils.assertFilePath("File must be equal", testFile, configFiles.get(0));
  }

  @Test
  public void testExecution_Expression() throws Exception {
    final PreprocessorContext context = new PreprocessorContext(new File("some_impossible_folder_121212"));

    final File testFile = new File(this.getClass().getResource("./global_variable_def.txt").toURI());

    final String path = testFile.getParent().replace('\'', '/').replace("\"", "\\\"");

    final String param = "@@\"" + path.replace("\\", "\\\\") + "\"+\"/\"+\"" + "global_variable_def.txt" + "\"";

    HANDLER.processCommandLineKey(param, context);

    final List<File> configFiles = context.getConfigFiles();

    assertEquals("File must be added", 1, configFiles.size());
    assertEquals("File must be equal", testFile, configFiles.get(0));
  }

  @Test(expected = PreprocessorException.class)
  public void testExecution_nonExistingFileWithExpression() {
    final PreprocessorContext context = new PreprocessorContext(new File("some_impossible_folder_121212"));
    HANDLER.processCommandLineKey("@@\"undefinded_file.111111.txtt\"", context);
  }

  @Test(expected = PreprocessorException.class)
  public void testExecution_nonExistingFile() {
    final PreprocessorContext context = new PreprocessorContext(new File("some_impossible_folder_121212"));
    HANDLER.processCommandLineKey("@undefinded_file.111111.txtt", context);
  }

  @Test(expected = PreprocessorException.class)
  public void testExecution_emptyFile() {
    final PreprocessorContext context = new PreprocessorContext(new File("some_impossible_folder_121212"));
    HANDLER.processCommandLineKey("@", context);
  }

  @Test(expected = PreprocessorException.class)
  public void testExecution_emptyFileForExpressionMode() {
    final PreprocessorContext context = new PreprocessorContext(new File("some_impossible_folder_121212"));
    HANDLER.processCommandLineKey("@@", context);
  }

  @Override
  public void testName() {
    assertEquals("@", HANDLER.getKeyName());
  }

  @Override
  public void testDescription() {
    assertDescription(HANDLER);
  }
}
