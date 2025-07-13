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

package com.igormaznitsa.jcp.expression.functions;

import static com.igormaznitsa.jcp.utils.IOUtils.closeQuietly;
import static com.igormaznitsa.jcp.utils.PreprocessorUtils.findFirstActiveFileContainer;

import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.context.CommentRemoverType;
import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

/**
 * The Function makes preprocessing of a file and return result as a string value. It uses the current preprocessor context as the context for preprocessing the file.
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public class FunctionEVALFILE extends AbstractFunction {

  private static final ValueType[][] ARG_TYPES = new ValueType[][] {{ValueType.STRING}};

  @Override

  public String getName() {
    return "evalfile";
  }

  @Override

  public String getReference() {
    return "preprocess file and get result as string";
  }

  @Override
  public int getArity() {
    return 1;
  }

  @Override


  public ValueType[][] getAllowedArgumentTypes() {
    return ARG_TYPES;
  }

  @Override

  public ValueType getResultType() {
    return ValueType.STRING;
  }


  private PreprocessorContext prepareContext(final PreprocessorContext base) {
    final PreprocessorContext result = new PreprocessorContext(base);
    result.setDryRun(true);
    result.setKeepLines(false);
    result.setClearTarget(false);
    result.setKeepComments(CommentRemoverType.REMOVE_C_STYLE);
    result.setCareForLastEol(true);
    return result;
  }


  public Value executeStr(final PreprocessorContext context, final Value strFilePath) {
    final String filePath = strFilePath.asString();

    final File fileToEvaluate;
    try {
      fileToEvaluate = context.findFileInSources(filePath);
    } catch (IOException ex) {
      throw context.makeException("Can't get get source file '" + filePath + '\'', null);
    }

    if (context.isVerbose()) {
      context.logForVerbose("Eval file '" + fileToEvaluate + '\'');
    }

    try {
      final FileInfoContainer fileContainer =
          new FileInfoContainer(fileToEvaluate, fileToEvaluate.getName(), false);

      final PreprocessorContext evalContext = this.prepareContext(context);
      final PreprocessingState state =
          fileContainer.preprocessFileWithNotification(null, evalContext, false);

      findFirstActiveFileContainer(context)
          .ifPresent(f -> {
            f.getIncludedSources().add(fileToEvaluate);
            f.getIncludedSources().addAll(evalContext.findAllInputFiles());
            f.getGeneratedResources().addAll(evalContext.findAllProducedFiles());
          });

      final StringWriter strWriter = new StringWriter(1024);
      state.writePrinterBuffers(strWriter);
      closeQuietly(strWriter);
      return Value.valueOf(strWriter.toString());
    } catch (Exception ex) {
      throw context.makeException("Unexpected exception", ex);
    }
  }
}
