/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.jcp.expression.functions;

import javax.annotation.Nonnull;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.meta.annotation.MustNotContainNull;

/**
 * The class implements the ISSUBSTR function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionISSUBSTR extends AbstractFunction {

  private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING, ValueType.STRING}};

  @Override
  @Nonnull
  public String getName() {
    return "issubstr";
  }

  @Nonnull
  public Value executeStrStr(@Nonnull final PreprocessorContext context, @Nonnull final Value subStrValue, @Nonnull final Value strValue) {
    final String str = strValue.asString().toLowerCase();
    final String subStr = subStrValue.asString().toLowerCase();
    return Value.valueOf(Boolean.valueOf(str.indexOf(subStr) >= 0));
  }

  @Override
  public int getArity() {
    return 2;
  }

  @Override
  @Nonnull
  @MustNotContainNull
  public ValueType[][] getAllowedArgumentTypes() {
    return ARG_TYPES;
  }

  @Override
  @Nonnull
  public String getReference() {
    return "check that substring presented in string (case insensitive)";
  }

  @Override
  @Nonnull
  public ValueType getResultType() {
    return ValueType.BOOLEAN;
  }

}
