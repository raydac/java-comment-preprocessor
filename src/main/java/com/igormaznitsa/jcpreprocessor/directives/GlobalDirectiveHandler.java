package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class GlobalDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "global";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getExpressionType() {
        return "VAL_NAME=EXPR";
    }
    
    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context){
        processLocalDefinition(string, context);
        return AfterProcessingBehaviour.PROCESSED;
    }

    @Override
    public String getReference() {
        return "it allows to set a global variable value";
    }

    @Override
    public boolean isGlobalPhaseAllowed() {
        return true;
    }

    @Override
    public boolean isPreprocessingPhaseAllowed() {
        return false;
    }
    
    private void processLocalDefinition(final String _str, final PreprocessorContext context) {
        final String[] splitted = PreprocessorUtils.splitForChar(_str, '=');

        if (splitted.length != 2) {
            throw new RuntimeException("Can't recognize the expression");
        }

        final Value p_value = Expression.eval(splitted[1].trim(), context);

        if (p_value == null) {
            throw new RuntimeException("Unsupported expression result");
        }

        context.setGlobalVariable(splitted[0].trim(), p_value);
    }
}
