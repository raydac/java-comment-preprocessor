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

/**
 * The class implements the str2int function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionSTR2INT extends AbstractStrConverter {

  @Override
  @Nonnull
  public String getName() {
    return "str2int";
  }

  @Override
  @Nonnull
  public Value executeStr(@Nonnull final PreprocessorContext context, @Nonnull final Value value) {
    return Value.valueOf(Long.parseLong(value.asString().trim()));
  }

  @Override
  @Nonnull
  public String getReference() {
    return "convert string into integet number";
  }

  @Override
  @Nonnull
  public ValueType getResultType() {
    return ValueType.INT;
  }
}
