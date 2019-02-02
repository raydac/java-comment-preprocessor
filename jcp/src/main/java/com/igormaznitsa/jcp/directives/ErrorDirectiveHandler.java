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
import com.igormaznitsa.jcp.utils.PreprocessorUtils;

import javax.annotation.Nonnull;

/**
 * The class implements //#error directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class ErrorDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  @Nonnull
  public String getName() {
    return "error";
  }

  @Override
  @Nonnull
  public DirectiveArgumentType getArgumentType() {
    return DirectiveArgumentType.EXPRESSTION;
  }

  @Override
  @Nonnull
  public String getReference() {
    return "throw fatal preprocessor exception with message and stop work";
  }

  protected void process(@Nonnull final PreprocessorContext context, @Nonnull final String message) {
    final String text = PreprocessorUtils.processMacroses(message, context);
    context.logError(text);
    throw context.makeException(text, null);
  }

  @Override
  @Nonnull
  public AfterDirectiveProcessingBehaviour execute(@Nonnull final String trimmedString, @Nonnull final PreprocessorContext context) {
    final String message = trimmedString.isEmpty() ? "Thrown fatal error" : Expression.evalExpression(trimmedString, context).toString();
    process(context, message);
    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }
}
