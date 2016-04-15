/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.jcp.directives;

import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;
import javax.annotation.Nonnull;

import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.containers.PreprocessingFlag;
import com.igormaznitsa.jcp.context.PreprocessorContext;

/**
 * The class implements the //#endif directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class EndIfDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  @Nonnull
  public String getName() {
    return "endif";
  }

  @Override
  public boolean executeOnlyWhenExecutionAllowed() {
    return false;
  }

  @Override
  @Nonnull
  public String getReference() {
    return "end of " + DIRECTIVE_PREFIX + "if..." + getFullName() + " control structure";
  }

  @Override
  @Nonnull
  public AfterDirectiveProcessingBehaviour execute(@Nonnull final String string, @Nonnull final PreprocessorContext context) {
    final PreprocessingState state = assertNotNull(context.getPreprocessingState());
    if (state.isIfStackEmpty()) {
      throw context.makeException("Detected "+getFullName() + " without " + DIRECTIVE_PREFIX + "if",null);
    }

    if (!state.isDirectiveCanBeProcessed() && state.isAtActiveIf()) {
      state.getPreprocessingFlags().remove(PreprocessingFlag.IF_CONDITION_FALSE);
    }

    state.popIf();

    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }
}
