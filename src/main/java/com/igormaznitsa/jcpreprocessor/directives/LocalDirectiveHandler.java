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
    public boolean hasExpression() {
        return true;
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
    public String getExpressionType() {
        return "VAR_NAME=EXPR";
    }

    private void processLocalDefinition(final String string, final PreprocessorContext context, final PreprocessingState state) {
        final String[] splitted = PreprocessorUtils.splitForChar(string, '=');

        if (splitted.length != 2) {
            throw new RuntimeException("Can't recognize an expression");
        }

        final Value value = Expression.eval(splitted[1].trim(), context, state);

        if (value == null) {
            throw new RuntimeException("Unsupported expression result");
        }

        context.setLocalVariable(splitted[0].trim().toLowerCase(), value);
    }
}
