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

package com.igormaznitsa.jcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import com.igormaznitsa.jcp.cmdline.CommandLineHandler;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public final class JCPreprocessorTest {

  private void assertGVDFPreprocessorException(final String file,
                                               final int stringIndexStartedFromOne)
      throws Exception {
    final PreprocessorContext context =
        new PreprocessorContext(new File("some_impossible_folder_121212"));
    context.registerConfigFile(new File(this.getClass().getResource(file).toURI()));
    final JcpPreprocessor preprocessor = new JcpPreprocessor(context);
    try {
      preprocessor.processConfigFiles();
      fail("Must throw a PreprocessorException");
    } catch (PreprocessorException expected) {
      if (stringIndexStartedFromOne != expected.getLineNumber()) {
        fail("Wrong error string index [" + expected.toString() + ']');
      }
    }
  }

  @Test
  public void testProcessGlobalVarDefiningFiles() throws Exception {
    final PreprocessorContext context =
        new PreprocessorContext(new File("some_impossible_folder_121212"));
    context.registerConfigFile(new File(this.getClass().getResource("./global_ok.txt").toURI()));
    final JcpPreprocessor preprocessor = new JcpPreprocessor(context);
    preprocessor.processConfigFiles();

    assertEquals("Must have the variable", "hello world",
        context.findVariableForName("globalVar1", true).asString());
    assertEquals("Must have the variable", Value.INT_THREE,
        context.findVariableForName("globalVar2", true));
    assertEquals("Character input encoding must be changed", StandardCharsets.ISO_8859_1,
        context.getSourceEncoding());
  }

  @Test
  public void testProcessGlobalVarDefiningFiles_ATsymbol() throws Exception {
    assertGVDFPreprocessorException("global_error_at.txt", 8);
  }

  @Test
  public void testJavaCommentRemoving() throws Exception {

    final File testDirectory = new File(getClass().getResource("removers/java").toURI());
    final File resultFile = new File(testDirectory, "w_o_comments.ttt");
    final File etalonFile = new File(testDirectory, "etalon.etl");

    if (resultFile.exists()) {
      assertTrue("We have to remove the existing result file", resultFile.delete());
    }

    final PreprocessorContext context =
        new PreprocessorContext(new File("some_impossible_folder_121212"));
    context.setSources(Collections.singletonList(testDirectory.getCanonicalPath()));
    context.setTarget(testDirectory);
    context.setClearTarget(false);
    context.setKeepComments(false);
    context.setExtensions(Collections.singletonList("ppp"));
    context.setExcludeExtensions(Collections.singletonList("etl"));

    final JcpPreprocessor preprocessor = new JcpPreprocessor(context);
    preprocessor.execute();

    assertTrue("There must be the result file", resultFile.exists());
    assertTrue("There must be the etalon file", etalonFile.exists());

    String differentLine = null;
    int lineIndex = 1;
    try (BufferedReader resultReader = new BufferedReader(
        new InputStreamReader(new FileInputStream(resultFile), StandardCharsets.UTF_8))) {
      try (BufferedReader etalonReader = new BufferedReader(
          new InputStreamReader(new FileInputStream(etalonFile),
              StandardCharsets.UTF_8))) {

        while (!Thread.currentThread().isInterrupted()) {
          final String resultStr = resultReader.readLine();
          final String etalonStr = etalonReader.readLine();
          if (resultStr == null && etalonStr == null) {
            break;
          }

          if (resultStr == null || !resultStr.equals(etalonStr)) {
            differentLine = resultStr;
            break;
          }

          lineIndex++;
        }
      }
    }

    if (differentLine != null) {
      fail("Line " + lineIndex + " There is a different strings [" + differentLine + '[');
    }
  }

  @Test
  public void testCLIHandlerNameConflicts() {
    final List<String> checked = new ArrayList<>();
    for (final CommandLineHandler h : JcpPreprocessor.COMMAND_LINE_HANDLERS) {
      final String name = h.getKeyName();
      for (final String l : checked) {
        if (l.startsWith(name) || name.startsWith(l)) {
          fail("Conflict [" + l + " and " + name + ']');
        }
      }
      checked.add(name);
    }
  }
}
