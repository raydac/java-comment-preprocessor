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
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

public class GlobalIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "_if";
    }

    @Override
    public String getReference() {
        return "it starts a global //#_if..//#_else..//#_endif construction";
    }
 
    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.BOOLEAN;
    }
    
    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

    @Override
    public boolean isGlobalPhaseAllowed() {
        return true;
    }

    @Override
    public boolean isPreprocessingPhaseAllowed() {
        return false;
    }
    
    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context) {
        if (state.isDirectiveCanBeProcessed()){
            final Value expressionResult = Expression.evalExpression(string,context,state);
            if (expressionResult == null || expressionResult.getType() != ValueType.BOOLEAN) {
                throw new RuntimeException(DIRECTIVE_PREFIX+"_if needs a boolean expression");
            }
            state.pushIf(true);
            if (!expressionResult.asBoolean().booleanValue()){
                state.getPreprocessingFlags().add(PreprocessingFlag.IF_CONDITION_FALSE);
            }
        }else{
            state.pushIf(false);
        }
 
        return AfterProcessingBehaviour.PROCESSED;
    }
}
