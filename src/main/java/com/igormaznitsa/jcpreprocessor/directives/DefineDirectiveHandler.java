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
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

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
        return "it allows to define a global (!) variable during the second pass, the variable will be set to TRUE";
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context) {
        final String name = string.trim().toLowerCase();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Variable name is empty");
        }

        boolean hasWrongChar = false;

        if (!Character.isLetter(name.charAt(0))) {
            hasWrongChar = true;
        } else {
            for (final char chr : name.toCharArray()) {
                if (!PreprocessorUtils.isCharAllowedInVariableOrFunctionName(chr)) {
                    hasWrongChar = true;
                    break;
                }
            }
        }
        if (hasWrongChar) {
            throw new IllegalArgumentException("Disallowed variable name [" + name + ']');
        }

        if (context.findVariableForName(name, state) != null) {
            context.warning("Variable \'"+name+"\' already defined");
        }
        context.setGlobalVariable(name, Value.BOOLEAN_TRUE,state);
        return AfterProcessingBehaviour.PROCESSED;
    }
}
