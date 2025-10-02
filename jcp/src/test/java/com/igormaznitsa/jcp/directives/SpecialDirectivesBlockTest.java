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
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

// This test checks work of //$$""", //$"""
// Those directives are very specific and they don't have any distinguished handler
public class SpecialDirectivesBlockTest extends AbstractDirectiveHandlerAcceptanceTest {

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
        final FilePositionInfo positionInfo =
            context.getPreprocessingState().findFilePositionInfo().orElseThrow();
        assertTrue(positionInfo.getLineNumber() >= 0);
        assertNotNull(uncommentedText);
        assertNotNull(context);

        calledForText.append("\n...\n").append(uncommentedText);

        final String indent = context.isPreserveIndents() ? " ".repeat(recommendedIndent) : "";

        return Arrays.stream(uncommentedText.split("\\R"))
            .map(x -> indent + x)
            .collect(Collectors.joining(context.getEol()));
      }
    };

    assertFilePreprocessing("directive_special_block.txt", false, true, null, null,
        c -> {
          c.setPreserveIndents(true);
          c.addCommentTextProcessor(testProcessor);
        });
    assertTrue(started.get());
    assertTrue(stopped.get());
    assertEquals("\n...\n" +
            "      hello 223 world\n" +
            "      next" +
            "\n...\n" +
            "       hello /*$111+112$*/ world\n" +
            "       next/*$111+112$*/" +
            "\n...\n" +
            "       hello /*$111+112$*/ world\n" +
            "      middle\n" +
            "       next/*$111+112$*/" +
            "\n...\n" +
            "       hello /*$111+112$*/ world" +
            "\n...\n" +
            "   split" +
            "\n...\n" +
            "       next/*$111+112$*/" +
            "\n...\n" +
            "      hello 223 world" +
            "\n...\n" +
            "    split" +
            "\n...\n" +
            "      next223" +
            "\n...\n" +
            "      line1" +
            "\n...\n" +
            "      line2" +
            "\n...\n" +
            "      hello\n" +
            "      world earth"
        , calledForText.toString());
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
