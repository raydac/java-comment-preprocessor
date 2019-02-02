/*
 * Copyright 2002-2019 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.igormaznitsa.jcp.expression;

import javax.annotation.Nonnull;

import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

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
  public String toString() {
    return this.variableName;
  }

}
