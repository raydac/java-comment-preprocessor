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

import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.containers.PreprocessingFlag;
import com.igormaznitsa.jcp.context.PreprocessorContext;

/**
 * The class implements the //#ifdefined directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class IfDefinedDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  public String getName() {
    return "ifdefined";
  }

  @Override
  public String getReference() {
    return "works similar //#if but needs only a variable name to check that it has been defined";
  }

  @Override
  public boolean executeOnlyWhenExecutionAllowed() {
    return false;
  }

  @Override
  public DirectiveArgumentType getArgumentType() {
    return DirectiveArgumentType.VARNAME;
  }

  @Override
  public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
    final PreprocessingState state = context.getPreprocessingState();
    if (state.isDirectiveCanBeProcessed()) {
      if (string.isEmpty()) {
        throw new IllegalArgumentException(DIRECTIVE_PREFIX + "ifdefined needs a variable");
      }
      state.pushIf(true);
      final boolean definitionFlag = context.findVariableForName(string) != null;
      if (!definitionFlag) {
        state.getPreprocessingFlags().add(PreprocessingFlag.IF_CONDITION_FALSE);
      }
    }
    else {
      state.pushIf(false);
    }

    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }
}
