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

package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.File;

/**
 * The handler for '@' prefixed files in the command string
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class GlobalVariableDefiningFileHandler implements CommandLineHandler {

  private static final String ARG_NAME = "@";

  @Override
  public String getDescription() {
    return "load global variable list from file defined by either path or expression (last one needs @@)";
  }

  @Override
  public boolean processCommandLineKey(final String key, final PreprocessorContext context) {
    boolean result = false;

    if (!key.isEmpty() && key.charAt(0) == '@') {
      String stringRest = PreprocessorUtils.extractTrimmedTail(ARG_NAME, key);

      if (stringRest.isEmpty()) {
        throw context.makeException("Empty string", null);
      }

      File file;

      if (stringRest.charAt(0) == '@') {
        stringRest = PreprocessorUtils.extractTrimmedTail("@", stringRest);

        if (context.isVerbose()) {
          context.logForVerbose(
              "Global parameter file defined through expression \'" + stringRest + '\'');
        }

        final Value resultValue = Expression.evalExpression(stringRest, context);

        final String fileName = resultValue.toString();
        file = new File(fileName);
      } else {
        file = new File(stringRest);
      }

      if (context.isVerbose()) {
        context.logForVerbose(
            "Reading global definition file [" + PreprocessorUtils.getFilePath(file) + "]  \'" +
                stringRest + '\'');
      }
      if (file.isFile()) {
        context.registerConfigFile(file);
      } else {
        throw context.makeException(
            "Can't find the global definition file \'" + PreprocessorUtils.getFilePath(file) + '\'',
            null);
      }

      result = true;
    }
    return result;
  }

  @Override
  public String getKeyName() {
    return ARG_NAME;
  }
}
