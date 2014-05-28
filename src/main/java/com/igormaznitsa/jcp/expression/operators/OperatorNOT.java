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

import com.igormaznitsa.jcp.expression.ExpressionItemPriority;
import com.igormaznitsa.jcp.expression.Value;

/**
 * The class implements the NOT operator handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class OperatorNOT extends AbstractOperator {

  @Override
  public int getArity() {
    return 1;
  }

  @Override
  public String getReference() {
    return "makes NOT operation over an operand, bitwise NOT over a numeric operand and logical NOT over a boolean one";
  }

  @Override
  public String getKeyword() {
    return "!";
  }

  public Value executeInt(final Value arg1) {
    return Value.valueOf(Long.valueOf(0xFFFFFFFFFFFFFFFFL ^ arg1.asLong().longValue()));
  }

  public Value executeBool(final Value arg1) {
    return Value.valueOf(Boolean.valueOf(!arg1.asBoolean().booleanValue()));
  }

  public ExpressionItemPriority getExpressionItemPriority() {
    return ExpressionItemPriority.FUNCTION;
  }
}
