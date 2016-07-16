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
 * The class implements the IS function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionIS extends AbstractFunction {

  private static final ValueType[][] SIGNATURES = new ValueType[][]{{ValueType.STRING,ValueType.ANY}};

  @Override
  @Nonnull
  public String getName() {
    return "is";
  }

  @Nonnull
  public Value executeStrAny(@Nonnull final PreprocessorContext context, @Nonnull final Value varName, @Nonnull final Value value) {
    final Value currentValue = context.findVariableForName(varName.asString());

    Value result = Value.BOOLEAN_FALSE;
    
    if (currentValue != null) {
      result =  value.toString().compareTo(currentValue.toString()) == 0 ? Value.BOOLEAN_TRUE : Value.BOOLEAN_FALSE;
    }
    
    return result;
  }

  @Override
  public int getArity() {
    return 2;
  }

  @Override
  @Nonnull
  @MustNotContainNull
  public ValueType[][] getAllowedArgumentTypes() {
    return SIGNATURES;
  }

  @Override
  @Nonnull
  public String getReference() {
    return "check that variable exists and compare value";
  }

  @Override
  @Nonnull
  public ValueType getResultType() {
    return ValueType.BOOLEAN;
  }

}
