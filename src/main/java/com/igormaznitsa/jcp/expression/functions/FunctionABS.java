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
 * The class implements the abs function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionABS extends AbstractFunction {

  private static final ValueType[][] ARGUMENT_TYPES = new ValueType[][]{{ValueType.INT}, {ValueType.FLOAT}};

  @Override
  @Nonnull
  public String getName() {
    return "abs";
  }

  @Nonnull
  public Value executeInt(@Nonnull final PreprocessorContext context, @Nonnull final Value value) {
    return Value.valueOf(Long.valueOf((Math.abs(value.asLong().longValue()))));
  }

  @Nonnull
  public Value executeFloat(@Nonnull final PreprocessorContext context, @Nonnull final Value value) {
    return Value.valueOf(Float.valueOf((Math.abs(value.asFloat().floatValue()))));
  }

  @Override
  public int getArity() {
    return 1;
  }

  @Override
  @Nonnull
  @MustNotContainNull
  public ValueType[][] getAllowedArgumentTypes() {
    return ARGUMENT_TYPES;
  }

  @Override
  @Nonnull
  public String getReference() {
    return "get absolute value of numeric value";
  }

  @Override
  @Nonnull
  public ValueType getResultType() {
    return ValueType.ANY;
  }

}
