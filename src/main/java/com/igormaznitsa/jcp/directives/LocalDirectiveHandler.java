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

import javax.annotation.Nonnull;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;

/**
 * The class implements the //#local directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class LocalDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  @Nonnull
  public String getName() {
    return "local";
  }

  @Override
  @Nonnull
  public AfterDirectiveProcessingBehaviour execute(@Nonnull final String string, @Nonnull final PreprocessorContext context) {
    processLocalDefinition(string, context);
    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }

  @Override
  @Nonnull
  public String getReference() {
    return "make local definition which is visilble only in the current file context";
  }

  @Override
  @Nonnull
  public DirectiveArgumentType getArgumentType() {
    return DirectiveArgumentType.SET;
  }

  private void processLocalDefinition(@Nonnull final String string, @Nonnull final PreprocessorContext context) {
    final String[] splitted = PreprocessorUtils.splitForEqualChar(string);

    if (splitted.length != 2) {
      throw context.makeException("Can't find expression", null);
    }

    final String name = splitted[0];
    final Value value = Expression.evalExpression(splitted[1], context);

    context.setLocalVariable(name, value);
  }
}
