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

package com.igormaznitsa.jcp.context;

import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.meta.annotation.MustNotContainNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The interface describes a special variable processor which will be called for
 * variables met by a preprocessor in expressions
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public interface SpecialVariableProcessor {

  /**
   * Get all variable names allowed by the processor as an array, all names must
   * be in lower case
   *
   * @return allowed variable names as a String array
   */
  @Nonnull
  @MustNotContainNull
  String[] getVariableNames();

  /**
   * Get the value for the variable
   *
   * @param varName the variable name, must not be null
   * @param context the preprocessor context, it can be null
   * @return the value, it must not return null because it will notified
   * preprocessor that it supports the variable
   */
  @Nonnull
  Value getVariable(@Nonnull String varName, @Nullable PreprocessorContext context);

  /**
   * Set a value to the variable
   *
   * @param varName the variable name, must not be null
   * @param value   the value to be set to the variable, must not be null
   * @param context the preprocessor context, it can be null
   */
  void setVariable(@Nonnull String varName, @Nonnull Value value, @Nullable PreprocessorContext context);

}
