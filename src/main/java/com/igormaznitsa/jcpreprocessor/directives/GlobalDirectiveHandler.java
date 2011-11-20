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
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

/**
 * The class implements the //#global directive handler
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class GlobalDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "global";
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.SET;
    }
    
    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context, final PreprocessingState state){
        processDefinition(string, context ,state);
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }

    @Override
    public String getReference() {
        return "allows to set a value for global variable during the first pass (the global phase)";
    }

    @Override
    public boolean isGlobalPhaseAllowed() {
        return true;
    }

    @Override
    public boolean isPreprocessingPhaseAllowed() {
        return false;
    }
    
    private void processDefinition(final String string, final PreprocessorContext context, final PreprocessingState state) {
        final String[] splitted = PreprocessorUtils.splitForSetOperator(string);

        if (splitted.length != 2) {
            throw new IllegalArgumentException("Can't recognize an expression ["+string+']');
        }

        final String name = splitted[0].trim();
        final Value value = Expression.evalExpression(splitted[1].trim(), context,state);

        context.setGlobalVariable(name,value,state);

        if (context.isVerbose() && context.containsGlobalVariable(name)){
                context.logWarning("Global value has been changed ["+name+'='+value+']');
        }
        
    }
}
