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
package com.igormaznitsa.jcp.directives;

import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.ExpressionItem;
import com.igormaznitsa.jcp.expression.ExpressionItemType;
import com.igormaznitsa.jcp.expression.ExpressionParser;
import com.igormaznitsa.jcp.expression.ExpressionTree;
import com.igormaznitsa.jcp.expression.ExpressionTreeElement;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.Variable;
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
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
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
        
        if (context.findVariableForName(name) != null) {
            context.logWarning("Variable \'"+name+"\' was already defined");
        }
        
        context.setGlobalVariable(name, Value.BOOLEAN_TRUE);
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
}
