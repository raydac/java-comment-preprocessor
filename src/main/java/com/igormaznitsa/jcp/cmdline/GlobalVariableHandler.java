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
package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;

/**
 * The handler for global variables, it adds met global variables into the inside storage
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class GlobalVariableHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/P:";

    @Override
    public String getDescription() {
        return "set a global variable, for instance /P:DEBUG=true (use $ instead \" char)";
    }

    @Override
    public boolean processCommandLineKey(final String key, final PreprocessorContext context) {
        boolean result = false;

        if (key != null && !key.isEmpty() && key.toUpperCase().startsWith(ARG_NAME)) {

            final String nameAndExpression = PreprocessorUtils.extractTrimmedTail(ARG_NAME, key);

            if (!nameAndExpression.isEmpty()) {

                final String[] splitted = PreprocessorUtils.splitForSetOperator(nameAndExpression);
                if (splitted.length != 2) {
                    throw new IllegalArgumentException("Wrong expression at a " + ARG_NAME + " directive [" + nameAndExpression + ']');
                }

                final String value = splitted[0];
                final String expression = splitted[1];

                if (context.containsGlobalVariable(value)) {
                    throw new IllegalArgumentException("Duplicated global definition detected [" + value + ']');
                }

                final Value resultVal = Expression.evalExpression(expression, context);
                context.setGlobalVariable(value, resultVal);
                result = true;
            }
        }
        return result;
    }

    @Override
    public String getKeyName() {
        return ARG_NAME;
    }
}
