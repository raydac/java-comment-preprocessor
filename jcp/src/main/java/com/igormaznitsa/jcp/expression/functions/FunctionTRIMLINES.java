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

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;

/**
 * The class implements the TRIMLINES function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionTRIMLINES extends AbstractStrConverter {

  @Override

  public String getName() {
    return "trimlines";
  }

  @Override

  public Value executeStr(final PreprocessorContext context, final Value value) {
    final String text = value.asString();
    final StringBuilder result = new StringBuilder(text.length());

    for (final String s : PreprocessorUtils.splitForChar(text, '\n')) {
      final String trimmed = s.trim();
      if (!trimmed.isEmpty()) {
        if (result.length() > 0) {
          result.append(PreprocessorUtils.getNextLineCodes());
        }
        result.append(trimmed);
      }
    }

    return Value.valueOf(result.toString());
  }

  @Override

  public String getReference() {
    return "trim each line found in string, remove empty lines";
  }

  @Override

  public ValueType getResultType() {
    return ValueType.STRING;
  }
}
