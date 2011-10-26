package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
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
    public DirectiveBehaviour execute(final String string, final ParameterContainer state, final PreprocessorContext context){
        processLocalDefinition(string, context);
        return DirectiveBehaviour.PROCESSED;
    }

    @Override
    public String getReference() {
        return "allows to define or change a local variable, it needs an expression";
    }

    private void processLocalDefinition(final String _str, final PreprocessorContext context) {
        final String[] splitted = PreprocessorUtils.splitForChar(_str, '=');

        if (splitted.length != 2) {
            throw new RuntimeException("Can't recognize an expression");
        }

        Value p_value = Expression.eval(splitted[1].trim(), context);

        if (p_value == null) {
            throw new RuntimeException("Unsupported expression result");
        }

        context.setLocalVariable(splitted[0].trim(), p_value);
    }
}
