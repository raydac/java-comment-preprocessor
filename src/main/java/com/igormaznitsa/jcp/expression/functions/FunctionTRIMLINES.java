/* 
 * Copyright 2017 Igor Maznitsa (http://www.igormaznitsa.com).
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

import com.igormaznitsa.jcp.utils.PreprocessorUtils;

/**
 * The class implements the TRIMLINES function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionTRIMLINES extends AbstractStrConverter {

  @Override
  @Nonnull
  public String getName() {
    return "trimlines";
  }

  @Override
  @Nonnull
  public Value executeStr(@Nonnull final PreprocessorContext context, @Nonnull final Value value) {
    final String text = value.asString();
    final StringBuilder result = new StringBuilder(text.length());

    for(final String s : PreprocessorUtils.splitForChar(text, '\n')){
      final String trimmed = s.trim();
      if (!trimmed.isEmpty()) {
        if (result.length()>0) result.append(PreprocessorUtils.getNextLineCodes());
        result.append(trimmed);
      }
    }
    
    return Value.valueOf(result.toString());
  }

  @Override
  @Nonnull
  public String getReference() {
    return "trim each line in string, empty lines will be removed";
  }

  @Override
  @Nonnull
  public ValueType getResultType() {
    return ValueType.STRING;
  }
}
