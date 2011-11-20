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

/**
 * The class implements the //#while directive handler
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class WhileDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "while";
    }

    @Override
    public String getReference() {
        return "makes a loop until "+DIRECTIVE_PREFIX+"end if its condition result is true";
    }

    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.BOOLEAN;
    }

    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext context, final PreprocessingState state) {
        if (state.isDirectiveCanBeProcessed()) {
            final Value condition = Expression.evalExpression(string,context,state);
            if (condition == null || condition.getType() != ValueType.BOOLEAN) {
                throw new IllegalArgumentException(DIRECTIVE_PREFIX+"while needs a boolean expression");
            }

            state.pushWhile(true);
            if (!condition.asBoolean().booleanValue())
            {
                state.getPreprocessingFlags().add(PreprocessingFlag.BREAK_COMMAND);
            }
        } else {
           state.pushWhile(false);
        }
        
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
}
