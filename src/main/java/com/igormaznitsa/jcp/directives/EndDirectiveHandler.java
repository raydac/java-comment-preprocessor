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

import com.igormaznitsa.jcp.containers.PreprocessingFlag;
import com.igormaznitsa.jcp.containers.TextFileDataContainer;
import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;

import javax.annotation.Nonnull;

import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

/**
 * The class implements the //#end directive
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class EndDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  @Nonnull
  public String getName() {
    return "end";
  }

  @Override
  @Nonnull
  public String getReference() {
    return "end of " + DIRECTIVE_PREFIX + "while..." + getFullName() + " loop, do jump to the loop start";
  }

  @Override
  @Nonnull
  public AfterDirectiveProcessingBehaviour execute(@Nonnull final String string, @Nonnull final PreprocessorContext context) {
    final PreprocessingState state = context.getPreprocessingState();
    if (state.isWhileStackEmpty()) {
      throw context.makeException("Detected " + getFullName() + " without " + DIRECTIVE_PREFIX + "while", null);
    }

    if (state.isDirectiveCanBeProcessedIgnoreBreak()) {
      final TextFileDataContainer thisWhile = assertNotNull("'WHILE' stack is empty!", state.peekWhile());
      final boolean breakIsSet = state.getPreprocessingFlags().contains(PreprocessingFlag.BREAK_COMMAND);
      state.popWhile();
      if (!breakIsSet) {
        state.goToString(thisWhile.getNextStringIndex());
      }
    } else {
      state.popWhile();
    }
    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }

  @Override
  public boolean executeOnlyWhenExecutionAllowed() {
    return false;
  }

}
