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

import com.igormaznitsa.jcp.containers.PreprocessingFlag;
import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;

/**
 * The class implements the //#break directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class BreakDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  public String getName() {
    return "break";
  }

  @Override
  public String getReference() {
    return "break the current " + DIRECTIVE_PREFIX + "while..." + DIRECTIVE_PREFIX + "end loop";
  }

  @Override
  public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
    final PreprocessingState state = context.getPreprocessingState();

    if (state.isWhileStackEmpty()) {
      throw context.makeException("Detected "+getFullName() + " without " + DIRECTIVE_PREFIX + "while",null);
    }

    state.getPreprocessingFlags().add(PreprocessingFlag.BREAK_COMMAND);
    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }
}
