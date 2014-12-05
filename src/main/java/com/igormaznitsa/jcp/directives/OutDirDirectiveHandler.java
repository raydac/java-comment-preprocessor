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

import com.igormaznitsa.jcp.context.JCPSpecialVariableProcessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;

/**
 * The class implements the //#outdir directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class OutDirDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  public String getName() {
    return "outdir";
  }

  @Override
  public String getReference() {
    return "change result file folder (works like change \'" + JCPSpecialVariableProcessor.VAR_DEST_DIR + "\')";
  }

  @Override
  public DirectiveArgumentType getArgumentType() {
    return DirectiveArgumentType.STRING;
  }

  @Override
  public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
    final Value name = Expression.evalExpression(string, context);
    final String nameAsString = name.toString();
    if (context.isVerbose()) {
      context.logForVerbose("Change result file folder '" + nameAsString + "\'");
    }
    context.getPreprocessingState().getRootFileInfo().setDestinationDir(nameAsString);
    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }
}
