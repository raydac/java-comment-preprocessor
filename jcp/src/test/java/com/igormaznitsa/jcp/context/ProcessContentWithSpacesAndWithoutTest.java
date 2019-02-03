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

import com.igormaznitsa.jcp.AbstractSpyPreprocessorContextTest;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Expression;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProcessContentWithSpacesAndWithoutTest extends AbstractSpyPreprocessorContextTest {

  @Test
  public void testProcess_NoSpaced_SpacesNotAllowed() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());
    final String text = Expression.evalExpression("evalfile(\"./standardFile.txt\")", context).asString();
    assertEquals(" hello\n /*$VAR$*/ Universe\nsome test", text);
  }

  @Test
  public void testProcess_NoSpaced_SpacesAllowed() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder(), () -> true);
    final String text = Expression.evalExpression("evalfile(\"./standardFile.txt\")", context).asString();
    assertEquals(" hello\n /*$VAR$*/ Universe\nsome test", text);
  }

  @Test
  public void testProcess_Spaced_SpacesNotAllowed() throws Exception {
    try {
      final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder());
      Expression.evalExpression("evalfile(\"./spacedFile.txt\")", context).asString();
      fail("Must throw preprocessor exception");
    } catch (PreprocessorException ex) {
      assertTrue(ex.getMessage().contains("Unknown variable"));
    }
  }

  @Test
  public void testProcess_Spaced_SpacesAllowed() throws Exception {
    final PreprocessorContext context = preparePreprocessorContext(getCurrentTestFolder(), () -> true);
    final String text = Expression.evalExpression("evalfile(\"./spacedFile.txt\")", context).asString();
    assertEquals(" hello\n /*$VAR$*/ Universe\nsome test", text);
  }


}
