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

public class ExitIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "exitif";
    }

    @Override
    public String getReference() {
        return "it interrupts the preprocessing if the excpression is true";
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.BOOLEAN;
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context) {
        // To end processing the file processing immediatly if the value is true
        final Value condition = Expression.evalExpression(string,context,state);
        if (condition == null || condition.getType() != ValueType.BOOLEAN) {
            throw new RuntimeException(DIRECTIVE_PREFIX+"exitif needs a boolean condition");
        }
        if (((Boolean) condition.getValue()).booleanValue()) {
            state.getPreprocessingFlags().add(PreprocessingFlag.END_PROCESSING);
            return AfterProcessingBehaviour.READ_NEXT_LINE;
        }
        return AfterProcessingBehaviour.PROCESSED;
    }

 }
