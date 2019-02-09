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

package com.igormaznitsa.jcp.exceptions;

import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public final class PreprocessorExceptionTest {

  @Test
  public void testExceptionStringIndex_WrongBracket() throws Exception {
    final File file = new File(this.getClass().getResource("wrong_bracket.txt").toURI());

    final PreprocessorContext context = new PreprocessorContext();
    context.setDryRun(true);

    final FileInfoContainer container = new FileInfoContainer(file, "test", false);
    try {
      container.preprocessFile(null, context);
      fail("Must throw PreprocessorException");
    } catch (PreprocessorException expected) {
      assertEquals("Expected correct line number", 17, expected.getLineNumber());
    }
  }

  @Test
  public void testExceptionStringIndex_WrongBracketClosing() throws Exception {
    final File file = new File(this.getClass().getResource("wrong_bracket_closing.txt").toURI());

    final PreprocessorContext context = new PreprocessorContext();
    context.setDryRun(true);

    final FileInfoContainer container = new FileInfoContainer(file, "test", false);
    try {
      container.preprocessFile(null, context);
      fail("Must throw PreprocessorException");
    } catch (PreprocessorException expected) {
      assertEquals("Expected correct line number", 17, expected.getLineNumber());
    }
  }

  @Test
  public void testExceptionStringIndex_WrongBracketInIncluded() throws Exception {
    final File file = new File(this.getClass().getResource("wrong_bracket_include.txt").toURI());

    final PreprocessorContext context = new PreprocessorContext();
    context.setSources(Collections.singletonList(file.getParent()));
    context.setDryRun(true);

    final FileInfoContainer container = new FileInfoContainer(file, "test", false);
    try {
      container.preprocessFile(null, context);
      fail("Must throw PreprocessorException");
    } catch (PreprocessorException expected) {
      final FilePositionInfo[] fileStack = expected.getIncludeChain();
      assertEquals("Must have depth 2", 2, fileStack.length);
      assertEquals("String index in the including file is 26", 26, fileStack[1].getLineNumber());
      assertEquals("String index in the wrong bracket file is 15", 17, fileStack[0].getLineNumber());

      assertEquals("Expected correct line number", 17, expected.getLineNumber());
    }
  }
}
