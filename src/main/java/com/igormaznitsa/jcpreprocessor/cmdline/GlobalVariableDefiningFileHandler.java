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
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.File;

/**
 * The handler for '@' prefixed files in the command string
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class GlobalVariableDefiningFileHandler implements CommandLineHandler {

    private static final String ARG_NAME = "@";

    @Override
    public String getDescription() {
        return "read parameters from a file defined as a path or as an expression (after @@)";
    }

    @Override
    public boolean processCommandLineKey(final String key, final PreprocessorContext context) {
        boolean result = false;

        if (key != null && !key.isEmpty() && key.charAt(0) == '@') {
            String stringRest = PreprocessorUtils.extractTrimmedTail(ARG_NAME, key);

            if (stringRest.isEmpty()) {
                throw new IllegalArgumentException("Empty string");
            }

            File file = null;

            if (stringRest.charAt(0) == '@') {
                stringRest = PreprocessorUtils.extractTrimmedTail("@", stringRest);

                final Value resultValue = Expression.evalExpression(stringRest, context, null);

                if (resultValue != null && resultValue.getType() == ValueType.STRING) {
                    final String fileName = resultValue.asString();

                    file = new File(fileName);

                } else {
                    throw new IllegalArgumentException("Wrong global variable file name expression [" + stringRest + ']');
                }
            } else {
                file = new File(stringRest);
            }

            if (file.exists() && file.isFile()) {
                context.addGlobalVarDefiningFile(file);
                if (context.isVerbose()) {
                    context.logInfo("A Global variable defining file has been added [" + PreprocessorUtils.getFilePath(file) + "] for the expression \'" + stringRest + '\'');
                }
            } else {
                throw new IllegalArgumentException("Can't find a global variable defining file \'" + PreprocessorUtils.getFilePath(file) + '\'');
            }

            result = true;
        }
        return result;
    }

    @Override
    public String getKeyName() {
        return ARG_NAME;
    }
}
