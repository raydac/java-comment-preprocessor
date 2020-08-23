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
import java.util.Locale;

/**
 * The class implements escape function handler to escape strings to be used inside preprocessor string value definitions.
 */
public final class FunctionESC extends AbstractFunction {

  private static final ValueType[][] ARG_TYPES = new ValueType[][] {{ValueType.STRING}};

  @Override

  public String getName() {
    return "esc";
  }


  public Value executeStr(final PreprocessorContext context, final Value source) {
    final String sourceString = source.asString();
    final StringBuilder result = new StringBuilder();
    for (int i = 0; i < sourceString.length(); i++) {
      final char chr = sourceString.charAt(i);
      switch (chr) {
        case '\n':
          result.append("\\n");
          break;
        case '\t':
          result.append("\\t");
          break;
        case '\b':
          result.append("\\b");
          break;
        case '\f':
          result.append("\\f");
          break;
        case '\r':
          result.append("\\r");
          break;
        case '\\':
          result.append("\\\\");
          break;
        case '\'':
          result.append("\\\'");
          break;
        case '\"':
          result.append("\\\"");
          break;
        default: {
          if (chr > 0x7F || Character.isISOControl(chr)) {
            String hexCode = Integer.toHexString(chr).toUpperCase(Locale.ENGLISH);
            if (hexCode.length() < 4) {
              hexCode = "0000".substring(0, 4 - hexCode.length()) + hexCode;
            }
            result.append("\\u").append(hexCode);
          } else {
            result.append(chr);
          }
        }
      }
    }
    return Value.valueOf(result.toString());
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

  public String getReference() {
    return "escape string chars";
  }

  @Override

  public ValueType getResultType() {
    return ValueType.STRING;
  }

}
