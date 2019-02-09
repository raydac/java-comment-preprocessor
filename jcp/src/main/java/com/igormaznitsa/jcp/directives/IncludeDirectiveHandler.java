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

import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * The class implements the //#include directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class IncludeDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  @Nonnull
  public String getName() {
    return "include";
  }

  @Override
  @Nonnull
  public String getReference() {
    return "include file content in current preprocess context";
  }

  @Override
  @Nonnull
  public DirectiveArgumentType getArgumentType() {
    return DirectiveArgumentType.STRING;
  }

  @Override
  @Nonnull
  public AfterDirectiveProcessingBehaviour execute(@Nonnull final String string, @Nonnull final PreprocessorContext context) {
    final PreprocessingState state = context.getPreprocessingState();
    final Value includingFilePath = Expression.evalExpression(string, context);

    final String filePath = includingFilePath.toString();

    try {
      final File theFile = context.findFileInSources(filePath);
      if (context.isVerbose()) {
        context.logForVerbose("Including file '" + theFile.getCanonicalPath() + '\'');
      }
      state.openFile(theFile);
    } catch (IOException ex) {
      throw context.makeException("Can't open file '" + filePath + '\'', ex);
    }
    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }
}
