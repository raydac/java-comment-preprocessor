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
import com.igormaznitsa.jcp.containers.PreprocessingFlag;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;

/**
 * The class implements the //#if directive handler
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class IfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "if";
    }

    @Override
    public String getReference() {
        return "allows to make "+DIRECTIVE_PREFIX+"if.."+DIRECTIVE_PREFIX+"else.."+DIRECTIVE_PREFIX+"endif construction, needs a boolean expression as the argument";
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
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context) {
        final PreprocessingState state = context.getPreprocessingState();
        if (state.isDirectiveCanBeProcessed()){
            final Value expressionResult = Expression.evalExpression(string,context);
            if (expressionResult == null || expressionResult.getType() != ValueType.BOOLEAN) {
                throw new IllegalArgumentException(DIRECTIVE_PREFIX+"if needs a boolean expression as the argument");
            }
            state.pushIf(true);
            if (!expressionResult.asBoolean().booleanValue()){
                state.getPreprocessingFlags().add(PreprocessingFlag.IF_CONDITION_FALSE);
            }
        }else{
            state.pushIf(false);
        }
 
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
}
