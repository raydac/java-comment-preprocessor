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
import com.igormaznitsa.jcp.expression.*;
import java.io.IOException;

/**
 * The class implements the //#define directive handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class DefineDirectiveHandler extends AbstractDirectiveHandler {

  @Override
  public String getName() {
    return "define";
  }

  @Override
  public DirectiveArgumentType getArgumentType() {
    return DirectiveArgumentType.VARNAME;
  }

  @Override
  public String getReference() {
    return "define a global(!) variable as TRUE by default (but also allowed expression after a space)";
  }

  protected void process(final PreprocessorContext context, final String varName, final Value value, final boolean exists){
    if (exists){
      context.logWarning("Variable \'" + varName + "\' has been already defined");
    }else{
      context.setGlobalVariable(varName, value);
    }
  }
  
  @Override
  public AfterDirectiveProcessingBehaviour execute(final String trimmedString, final PreprocessorContext context) {
    try {
      final int spaceIndex = trimmedString.indexOf(' ');
      final String name;
      final String expression;
      if (spaceIndex>0){
        name = trimmedString.substring(0,spaceIndex).trim();
        final String trimmed = trimmedString.substring(spaceIndex).trim();
        expression = trimmed.isEmpty() || trimmed.startsWith("//") || trimmed.startsWith("/*") ? null : trimmed;
      }else{
        name = trimmedString;
        expression = null;
      }
      
      final ExpressionTree nameTree = ExpressionParser.getInstance().parse(name, context);
      
      if (nameTree.isEmpty()){
        final String text = "Can't find variable name";
        throw new IllegalArgumentException(text, context.makeException(text, null));
      }

      final ExpressionTreeElement root = nameTree.getRoot();
      final ExpressionItem item = root.getItem();
      if (item.getExpressionItemType() != ExpressionItemType.VARIABLE) {
        final String text = "Can't recognize variable name ["+name+']';
        throw new IllegalArgumentException(text, context.makeException(text, null));
      }
      
      final Value value;
      
      if (expression!=null){
        value = Expression.evalExpression(expression, context);
      }else{
        value = Value.valueOf(Boolean.TRUE);
      }
      
      process(context, ((Variable) item).getName(), value,context.findVariableForName(name) != null);
    }
    catch (IOException ex) {
      final String text = "Can't recognize variable name [" + trimmedString + ']';
      throw new IllegalArgumentException(text, context.makeException(text, ex));
    }


    return AfterDirectiveProcessingBehaviour.PROCESSED;
  }
}
