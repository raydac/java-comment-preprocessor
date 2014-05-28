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
package com.igormaznitsa.jcp.context;

import com.igormaznitsa.jcp.expression.Value;

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
  String[] getVariableNames();

  /**
   * Get the value for the variable
   *
   * @param varName the variable name, must not be null
   * @param context the preprocessor context, it can be null
   * @return the value, it must not return null because it will notified
   * preprocessor that it supports the variable
   */
  Value getVariable(String varName, PreprocessorContext context);

  /**
   * Set a value to the variable
   *
   * @param varName the variable name, must not be null
   * @param value the value to be set to the variable, must not be null
   * @param context the preprocessor context, it can be null
   */
  void setVariable(String varName, Value value, PreprocessorContext context);

}
