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

import com.igormaznitsa.jcp.expression.ExpressionItem;
import com.igormaznitsa.jcp.expression.ExpressionItemType;

/**
 * The class is the base for all operator handlers
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public abstract class AbstractOperator implements ExpressionItem {

  /**
   * The constant is the prefix for executing methods of operators
   */
  public static final String EXECUTION_PREFIX = "execute";

  /**
   * The array contains all operators allowed by the preprocessor
   */
  public static final AbstractOperator[] ALL_OPERATORS = new AbstractOperator[]{
    new OperatorEQU(),
    new OperatorGREAT(),
    new OperatorGREATEQU(),
    new OperatorLESS(),
    new OperatorLESSEQU(),
    new OperatorNOTEQU(),
    new OperatorADD(),
    new OperatorSUB(),
    new OperatorMUL(),
    new OperatorDIV(),
    new OperatorMOD(),
    new OperatorNOT(),
    new OperatorAND(),
    new OperatorOR(),
    new OperatorXOR(),};

  /**
   * Find an operator handler for its class
   *
   * @param <E> the handler class extends AbstractOperator
   * @param operatorClass the class to be used for search, must not be null
   * @return an instance of the handler or null if there is not any such one
   */
  public static <E extends AbstractOperator> E findForClass(final Class<E> operatorClass) {
    for (final AbstractOperator operator : ALL_OPERATORS) {
      if (operator.getClass() == operatorClass) {
        return operatorClass.cast(operator);
      }
    }
    return null;
  }

  /**
   * Get the expression item type
   *
   * @return for operators it is always ExpressionItemType.OPERATOR
   */
  public ExpressionItemType getExpressionItemType() {
    return ExpressionItemType.OPERATOR;
  }

  /**
   * Get the operator arity
   *
   * @return the operator arity (1 or 2)
   */
  public abstract int getArity();

  /**
   * Get the operator keyword
   *
   * @return the operator keyword, must not be null
   */
  public abstract String getKeyword();

  /**
   * Get the operator reference to be shown for a help information request
   *
   * @return the operator reference as a String, must not be null
   */
  public abstract String getReference();

  @Override
  public String toString() {
    return "OPERATOR: " + getKeyword();
  }
}
