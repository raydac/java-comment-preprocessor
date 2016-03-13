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
package com.igormaznitsa.jcp.expression.operators;

import javax.annotation.Nonnull;

import com.igormaznitsa.jcp.expression.ExpressionItemPriority;
import com.igormaznitsa.jcp.expression.Value;

/**
 * The class implements the DIV operator handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class OperatorDIV extends AbstractOperator {

  @Override
  public int getArity() {
    return 2;
  }

  @Override
  @Nonnull
  public String getReference() {
    return "division operator";
  }

  @Override
  @Nonnull
  public String getKeyword() {
    return "/";
  }

  @Nonnull
  public Value executeIntInt(@Nonnull final Value arg1, @Nonnull final Value arg2) {
    return Value.valueOf(arg1.asLong() / arg2.asLong());
  }

  @Nonnull
  public Value executeIntFloat(@Nonnull final Value arg1, @Nonnull final Value arg2) {
    return Value.valueOf(arg1.asLong() / arg2.asFloat());
  }

  @Nonnull
  public Value executeFloatInt(@Nonnull final Value arg1, @Nonnull final Value arg2) {
    return Value.valueOf(arg1.asFloat() / arg2.asLong());
  }

  @Nonnull
  public Value executeFloatFloat(@Nonnull final Value arg1, @Nonnull final Value arg2) {
    return Value.valueOf(arg1.asFloat() / arg2.asFloat());
  }

  @Override
  @Nonnull
  public ExpressionItemPriority getExpressionItemPriority() {
    return ExpressionItemPriority.ARITHMETIC_MUL_DIV_MOD;
  }
}
