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

/**
 * The enumeration contains all allowed types for expression values and their
 * signatures
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public enum ValueType {

  ANY("Any"), STRING("Str"), BOOLEAN("Bool"), INT("Int"), FLOAT("Float"), UNKNOWN("Unknown");

  /**
   * The signature for the type it will be used in method calls
   */
  private final String signature;

  ValueType(final String signature) {
    this.signature = signature;
  }


  public String getSignature() {
    return this.signature;
  }

  /**
   * To check that the type is compatible with another one
   *
   * @param type the type to be checked, must not be null
   * @return true if the type is compatible else false
   */
  public boolean isCompatible(final ValueType type) {
    if (this == type) {
      return true;
    }

    return this != UNKNOWN && type != UNKNOWN && (this == ANY || type == ANY);

  }
}
