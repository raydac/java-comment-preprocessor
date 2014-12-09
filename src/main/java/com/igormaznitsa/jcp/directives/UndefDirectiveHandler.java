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
 * The class implements the //#undef directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class UndefDirectiveHandler extends DefineDirectiveHandler {

  @Override
  public String getName() {
    return "undef";
  }

  @Override
  public String getReference() {
    return "undefine either local or global variable if it is defined";
  }

  @Override
  protected void process(final PreprocessorContext context, final String varName, final Value value, final boolean exists) {
    if(context.isLocalVariable(varName)){
      context.removeLocalVariable(varName);
    }else if (context.isGlobalVariable(varName)){
      context.removeGlobalVariable(varName);
    }else{
      throw context.makeException("Attempting to undefine unknown variable '"+value+"\'",null);
    }
  }

}
