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

public class LocalDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "local";
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context){
        processLocalDefinition(string, context, state);
        return AfterProcessingBehaviour.PROCESSED;
    }

    @Override
    public String getReference() {
        return "allows to define or change a local variable, it needs an expression";
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.SET;
    }
    
    private void processLocalDefinition(final String string, final PreprocessorContext context, final PreprocessingState state) {
        final String[] splitted = PreprocessorUtils.splitForSetOperator(string);

        if (splitted.length != 2) {
            throw new RuntimeException("Can't recognize an expression");
        }

        final String name = splitted[0];
        final Value value = Expression.evalExpression(splitted[1], context, state);

        if (value == null) {
            throw new RuntimeException("Expression can't be calculated");
        }

        context.setLocalVariable(name, value);
    }
}
