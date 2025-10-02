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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.igormaznitsa.jcp.context.CommentTextProcessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import java.util.concurrent.atomic.AtomicBoolean;

// This test checks work of //$$, //$ and /*-*/
// Those directives are very specific and they don't have any distinguished handler
public class SpecialDirectivesTest extends AbstractDirectiveHandlerAcceptanceTest {

  @Override
  public void testExecution() throws Exception {
    final StringBuilder calledForText = new StringBuilder();

    final AtomicBoolean started = new AtomicBoolean();
    final AtomicBoolean stopped = new AtomicBoolean();

    final CommentTextProcessor testProcessor = new CommentTextProcessor() {
      @Override
      public void onContextStarted(PreprocessorContext context) {
        if (!started.compareAndSet(false, true)) {
          fail();
        }
      }

      @Override
      public void onContextStopped(PreprocessorContext context, Throwable error) {
        if (!stopped.compareAndSet(false, true)) {
          fail();
        }
      }

      @Override
      public boolean isAllowed(PreprocessorContext context) {
        return true;
      }

      @Override
      public String processUncommentedText(PreprocessorContext context, int recommendedIndent,
                                           String uncommentedText) {
        assertNotNull(uncommentedText);
        assertNotNull(context);

        calledForText.append("\n...\n").append(uncommentedText);

        return uncommentedText;
      }
    };

    assertFilePreprocessing("directive_special.txt", false, false, null, null,
        c -> c.addCommentTextProcessor(testProcessor));
    assertTrue(started.get());
    assertTrue(stopped.get());
    assertEquals("\n...\n" +
        "hello 223 world" +
        "\n...\n" +
        "hello /*$111+112$*/ world" +
        "\n...\n" +
        "\"\"\"hello 223 world" +
        "\n...\n" +
        "\"\"\"hello /*$111+112$*/ world", calledForText.toString());
  }

  @Override
  public void testKeyword() throws Exception {
    // do nothing because it is a group test
  }

  @Override
  public void testExecutionCondition() throws Exception {
    // do nothing because it is a group test
  }

  @Override
  public void testReference() throws Exception {
    // do nothing because it is a group test
  }

  @Override
  public void testArgumentType() throws Exception {
    // do nothing because it is a group test
  }

  @Override
  public void testPhase() throws Exception {
    // do nothing because it is a group test
  }

}
