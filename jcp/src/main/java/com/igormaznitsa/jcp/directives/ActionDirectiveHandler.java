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

import static com.igormaznitsa.jcp.utils.PreprocessorUtils.findLastActiveFileContainer;

import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.ExpressionItem;
import com.igormaznitsa.jcp.expression.ExpressionParser;
import com.igormaznitsa.jcp.expression.ExpressionTree;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The class implements the //#action directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class ActionDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  public String getName() {
    return "action";
  }

  @Override
  public String getReference() {
    return "call extension action with args";
  }

  @Override
  public DirectiveArgumentType getArgumentType() {
    return DirectiveArgumentType.MULTI_EXPRESSION;
  }

  @Override
  public AfterDirectiveProcessingBehaviour execute(final String string,
                                                   final PreprocessorContext context) {
    final List<PreprocessorExtension> extensions = context.getPreprocessorExtensions();

    if (extensions.isEmpty()) {
      throw context.makeException(
          "Detected action directive but there is no any provided action preprocessor extension to process it [" +
              string + ']',
          null);
    }

    try {
      final List<ExpressionTree> args = parseString(string, context);
      final PreprocessorExtension extension = extensions.stream()
          .filter(x -> x.isAllowed(
              findLastActiveFileContainer(context).orElseThrow(
                  () -> new IllegalStateException("Can't find active file container")),
              context.getPreprocessingState().findLastPositionInfoInStack().orElseThrow(
                  () -> new IllegalStateException("Can't find last position in include stack")),
              context,
              context.getPreprocessingState()
          ))
          .filter(x -> x.hasAction(args.size()))
          .findFirst().orElse(null);

      if (extension == null) {
        throw context.makeException(
            "Can't find any preprocessor extension to process action: " + string,
            null);
      }

      final Value[] results = new Value[args.size()];
      int index = 0;
      for (final ExpressionTree expr : args) {
        final Value val = Expression.evalTree(expr, context);
        results[index++] = val;
      }

      if (!extension.processAction(context, results)) {
        throw context.makeException("Unable to process an action", null);
      }
    } catch (IOException ex) {
      throw context.makeException("Unexpected string detected [" + string + ']', ex);
    }

    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }


  private List<ExpressionTree> parseString(final String str, final PreprocessorContext context)
      throws IOException {
    final ExpressionParser parser = ExpressionParser.getInstance();

    final PushbackReader reader = new PushbackReader(new StringReader(str));
    final List<ExpressionTree> result = new ArrayList<>();

    final PreprocessingState state = context.getPreprocessingState();
    final FilePositionInfo[] stack;
    final String sources;
    stack = state.makeIncludeStack();
    sources = state.getLastReadString();

    while (!Thread.currentThread().isInterrupted()) {
      final ExpressionTree tree;
      tree = new ExpressionTree(stack, sources);
      final ExpressionItem delimiter = parser.readExpression(reader, tree, context, false, true);

      if (delimiter != null && ExpressionParser.SpecialItem.COMMA != delimiter) {
        throw context.makeException("Wrong argument format detected", null);
      }

      if (tree.isEmpty()) {
        if (delimiter == null) {
          break;
        } else {
          throw context.makeException("Empty argument", null);
        }
      } else {
        result.add(tree);
        if (delimiter == null) {
          break;
        }
      }
    }

    return result;
  }
}
