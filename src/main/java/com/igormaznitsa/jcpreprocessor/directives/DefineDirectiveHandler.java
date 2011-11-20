/*
 * Copyright 2011 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307  USA
 */
package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionItem;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionItemType;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionParser;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionTree;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionTreeElement;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.Variable;
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
        return "allows to define a global (!) variable during the second pass (non-global), the variable will be set to the TRUE value";
    }

    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context, final PreprocessingState state) {
        String name = null;

        try {
            final ExpressionTree tree = ExpressionParser.getInstance().parse(string, context);
            if (tree.isEmpty()){
                throw new IllegalArgumentException("There is not any variable");
            }
            
            final ExpressionTreeElement root = tree.getRoot();
            final ExpressionItem item = root.getItem();
            if (item.getExpressionItemType() != ExpressionItemType.VARIABLE){
                throw new IllegalArgumentException("You must use a variable as the argument");
            }
            
            name = ((Variable)item).getName();
        }catch(IOException ex){
            throw new IllegalArgumentException("Can't parse the variable name ["+string+']',ex);
        }
        
        if (context.findVariableForName(name, state) != null) {
            context.logWarning("Variable \'"+name+"\' was already defined");
        }
        
        context.setGlobalVariable(name, Value.BOOLEAN_TRUE,state);
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
}
