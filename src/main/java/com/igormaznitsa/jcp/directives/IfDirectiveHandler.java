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
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;

/**
 * The class implements the //#if directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class IfDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  public String getName() {
    return "if";
  }

  @Override
  public String getReference() {
    return "check flag and start " + DIRECTIVE_PREFIX + "if.." + DIRECTIVE_PREFIX + "else.." + DIRECTIVE_PREFIX + "endif construction";
  }

  @Override
  public DirectiveArgumentType getArgumentType() {
    return DirectiveArgumentType.BOOLEAN;
  }

  @Override
  public boolean executeOnlyWhenExecutionAllowed() {
    return false;
  }

  @Override
  public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
    final PreprocessingState state = context.getPreprocessingState();
    if (state.isDirectiveCanBeProcessed()) {
      final Value expressionResult = Expression.evalExpression(string, context);
      if (expressionResult == null || expressionResult.getType() != ValueType.BOOLEAN) {
        throw context.makeException("Non boolean flag",null);
      }
      state.pushIf(true);
      if (!expressionResult.asBoolean()) {
        state.getPreprocessingFlags().add(PreprocessingFlag.IF_CONDITION_FALSE);
      }
    }
    else {
      state.pushIf(false);
    }

    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }
}
