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
package com.igormaznitsa.jcp.directives;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;

/**
 * The class implements the //#definel directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class DefinelDirectiveHandler extends DefineDirectiveHandler {

  @Override
  public String getName() {
    return "definel";
  }

  @Override
  public String getReference() {
    return "define local(!) variable as true (by default) or initialize it by expression result (if presented)";
  }

  @Override
  protected void process(final PreprocessorContext context, final String varName, final Value value, final boolean exists) {
      context.setLocalVariable(varName, value);
  }
}
