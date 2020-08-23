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

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.ExpressionItem;
import com.igormaznitsa.jcp.expression.ExpressionItemType;
import com.igormaznitsa.jcp.expression.ExpressionParser;
import com.igormaznitsa.jcp.expression.ExpressionTree;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.Variable;
import java.io.IOException;
import java.util.Objects;

/**
 * The class implements the //#define directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class DefineDirectiveHandler extends AbstractDirectiveHandler {

  @Override

  public String getName() {
    return "define";
  }

  @Override

  public DirectiveArgumentType getArgumentType() {
    return DirectiveArgumentType.TAIL;
  }

  @Override

  public String getReference() {
    return "add global BOOL variable and either set it TRUE (by default) or initialize by expression result";
  }

  protected void process(final PreprocessorContext context, final String varName, final Value value,
                         final boolean exists) {
    if (exists) {
      context.logWarning("Variable '" + varName + "' already defined");
    }
    context.setGlobalVariable(varName, value);
  }

  @Override

  public AfterDirectiveProcessingBehaviour execute(final String rawTail,
                                                   final PreprocessorContext context) {
    try {
      final String trimmedTail = rawTail.trim();
      final int spaceIndex = trimmedTail.indexOf(' ');
      final String name;
      final String expression;
      if (spaceIndex > 0) {
        name = trimmedTail.substring(0, spaceIndex).trim();
        final String trimmed = trimmedTail.substring(spaceIndex).trim();
        expression =
            trimmed.isEmpty() || trimmed.startsWith("//") || trimmed.startsWith("/*") ? null :
                trimmed;
      } else {
        name = trimmedTail;
        expression = null;
      }

      final ExpressionTree nameTree = ExpressionParser.getInstance().parse(name, context);

      if (nameTree.isEmpty()) {
        throw context.makeException("Var name is empty", null);
      }

      final ExpressionItem item = Objects.requireNonNull(nameTree.getRoot().getItem());
      if (item.getExpressionItemType() != ExpressionItemType.VARIABLE) {
        throw context.makeException("Can't recognize variable name [" + name + ']', null);
      }

      final Value value;

      if (expression != null) {
        value = Expression.evalExpression(expression, context);
      } else {
        value = Value.valueOf(Boolean.TRUE);
      }

      process(context, ((Variable) item).getName(), value,
          context.findVariableForName(name, true) != null);
    } catch (IOException ex) {
      throw context.makeException("Unexpected exception", ex);
    }

    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }
}
