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

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;

/**
 * The class implements the round function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionROUND extends AbstractFunction {

  private static final ValueType[][] SIGNATURES = new ValueType[][]{{ValueType.FLOAT}, {ValueType.INT}};

  @Override
  public String getName() {
    return "round";
  }

  public Value executeInt(final PreprocessorContext context, final Value value) {
    return value;
  }

  public Value executeFloat(final PreprocessorContext context, final Value value) {
    return Value.valueOf(Long.valueOf(Math.round(value.asFloat())));
  }

  @Override
  public int getArity() {
    return 1;
  }

  @Override
  public ValueType[][] getAllowedArgumentTypes() {
    return SIGNATURES;
  }

  @Override
  public String getReference() {
    return "round float value to nearest integer";
  }

  @Override
  public ValueType getResultType() {
    return ValueType.INT;
  }

}
