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

  public String getSignature() {
    return this.signature;
  }

  private ValueType(final String signature) {
    this.signature = signature;
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

    if (this == UNKNOWN || type == UNKNOWN) {
      return false;
    }

    return this == ANY || type == ANY;
  }
}
