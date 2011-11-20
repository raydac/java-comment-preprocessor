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
package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class GlobalVariableHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/P:";

    public String getDescription() {
        return "set a global variable, for instance /P:DEBUG=true";
    }

    public boolean processArgument(final String argument, final PreprocessorContext context) {
        boolean result = false;

        if (argument != null && !argument.isEmpty()) {
            if (argument.toUpperCase().startsWith(ARG_NAME)) {

                final String nameAndExpression = PreprocessorUtils.extractTrimmedTail(ARG_NAME, argument);

                if (!nameAndExpression.isEmpty()) {

                    final String[] splitted = PreprocessorUtils.splitForSetOperator(nameAndExpression);
                    if (splitted.length != 2) {
                        throw new RuntimeException("Wrong expression at a " + ARG_NAME + " directive [" + nameAndExpression + ']');
                    }

                    final String value = splitted[0];
                    final String expression = splitted[1];

                    if (context.containsGlobalVariable(value)) {
                        throw new IllegalArgumentException("Duplicated global definition detected [" + value + ']');
                    }

                    final Value resultVal = Expression.evalExpression(expression, context, null);
                    context.setGlobalVariable(value, resultVal, null);
                    result = true;
                }
            }
        }
        return result;
    }

    public String getKeyName() {
        return ARG_NAME;
    }
}
