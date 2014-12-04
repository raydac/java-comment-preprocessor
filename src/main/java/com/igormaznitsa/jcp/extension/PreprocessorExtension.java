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
package com.igormaznitsa.jcp.extension;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;

/**
 * The interface describes an extension which can be connected to a preprocessor
 * and to be notified about some calls and actions
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public interface PreprocessorExtension {

  /**
   * To process an action (it will be called if the preprocessor is met
   * //#action directive)
   *
   * @param context the current preprocessor context, must not be null
   * @param parameters the parameters of the action directive, must not be null
   * @return true if the action has been processed successfully or false, if it
   * is false then exception will be thrown and preprocessing will be stopped
   */
  public boolean processAction(PreprocessorContext context, Value[] parameters);

  /**
   * Call to process a user function (the function starts with $)
   *
   * @param functionName the name of the function (without $ and in low case),
   * must not be null
   * @param arguments the function arguments as an array, must not be null
   * @return a calculated value, it must not be null
   */
  public Value processUserFunction(String functionName, Value[] arguments);

  /**
   * When a preprocessor meets a user defined function (the function starts with
   * $) then it will ask for its arity
   *
   * @param functionName the function name without $ and in low case, must not
   * be null
   * @return the function arity (the argument number), zero or a great value
   */
  public int getUserFunctionArity(String functionName);
}
