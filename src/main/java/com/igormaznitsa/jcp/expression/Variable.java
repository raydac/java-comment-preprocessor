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
package com.igormaznitsa.jcp.expression;

import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

import javax.annotation.Nonnull;

/**
 * The class describes an expression variable
 *
 * @author Igor Mznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class Variable implements ExpressionItem {

  /**
   * The variable contains the expression variable name
   */
  private final String variableName;

  /**
   * The constructor
   *
   * @param varName the variable name, it must not be null
   */
  public Variable(@Nonnull final String varName) {
    assertNotNull("Var name is null", varName);
    this.variableName = varName;
  }

  /**
   * Get the variable name
   *
   * @return the name saved by the object
   */
  @Nonnull
  public String getName() {
    return this.variableName;
  }

  /**
   * Get the expression item type
   *
   * @return it returns only ExpressionItemType.VARIABLE
   */
  @Override
  @Nonnull
  public ExpressionItemType getExpressionItemType() {
    return ExpressionItemType.VARIABLE;
  }

  /**
   * Get the expression item priority
   *
   * @return it returns only ExpressionItemPriority.VALUE
   */
  @Override
  @Nonnull
  public ExpressionItemPriority getExpressionItemPriority() {
    return ExpressionItemPriority.VALUE;
  }

  @Override
  @Nonnull
  public String toString(){
    return this.variableName;
  }
  
}
