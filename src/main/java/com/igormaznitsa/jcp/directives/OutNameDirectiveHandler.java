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

import com.igormaznitsa.jcp.context.JCPSpecialVariableProcessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;

import javax.annotation.Nonnull;

/**
 * The class implements the //#outname directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class OutNameDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  @Nonnull
  public String getName() {
    return "outname";
  }

  @Override
  @Nonnull
  public String getReference() {
    return "change result file name (works like change \'" + JCPSpecialVariableProcessor.VAR_DEST_FILE_NAME + "\')";
  }

  @Override
  @Nonnull
  public DirectiveArgumentType getArgumentType() {
    return DirectiveArgumentType.STRING;
  }

  @Override
  @Nonnull
  public AfterDirectiveProcessingBehaviour execute(@Nonnull final String string, @Nonnull final PreprocessorContext context) {
    final Value fileName = Expression.evalExpression(string, context);
    final String fileNameAsStr = fileName.toString();
    if (context.isVerbose()) {
      context.logForVerbose("Change result file name to '" + fileNameAsStr + "\'");
    }
    context.getPreprocessingState().getRootFileInfo().setDestinationName(fileNameAsStr);
    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }
}
