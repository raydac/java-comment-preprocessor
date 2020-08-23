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

import static org.apache.commons.text.StringEscapeUtils.escapeJava;


import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.util.List;

/**
 * The class implements escape function handler to escape strings to be used in java.
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionSTR2JAVA extends AbstractFunction {

  private static final ValueType[][] ARG_TYPES =
      new ValueType[][] {{ValueType.STRING, ValueType.BOOLEAN}};

  @Override

  public String getName() {
    return "str2java";
  }


  public Value executeStrBool(final PreprocessorContext context, final Value source,
                              final Value splitAndQuoteLines) {
    if (splitAndQuoteLines.asBoolean()) {
      final boolean endsWithNextLine = source.asString().endsWith("\n");
      final List<String> split =
          PreprocessorUtils.splitForCharAndHoldEmptyLine(source.asString(), '\n');
      final StringBuilder result = new StringBuilder(source.asString().length() * 2);
      final String nextLineChars = PreprocessorUtils.getNextLineCodes();

      int index = 0;
      for (final String s : split) {
        final boolean last = ++index == split.size();
        if (result.length() > 0) {
          result.append(nextLineChars).append('+');
        }
        result.append('\"').append(escapeJava(s));
        if (last) {
          result.append(endsWithNextLine ? "\\n\"" : "\"");
        } else {
          result.append("\\n\"");
        }
      }
      return Value.valueOf(result.toString());
    } else {
      return Value.valueOf(escapeJava(source.asString()));
    }
  }

  @Override
  public int getArity() {
    return 2;
  }

  @Override


  public ValueType[][] getAllowedArgumentTypes() {
    return ARG_TYPES;
  }

  @Override

  public String getReference() {
    return "escape string for Java";
  }

  @Override

  public ValueType getResultType() {
    return ValueType.STRING;
  }

}
