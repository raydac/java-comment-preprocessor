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
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getExpressionType() {
        return "VAR_NAME";
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
