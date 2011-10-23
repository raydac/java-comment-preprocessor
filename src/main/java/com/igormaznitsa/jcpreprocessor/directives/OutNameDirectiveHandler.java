package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.IOException;

public class OutNameDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "outname";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getReference() {
        return null;
    }

    @Override
    public DirectiveBehaviourEnum execute(String string, ParameterContainer state, PreprocessorContext context) {
        Value p_value = Expression.eval(string, context);

        if (p_value == null || p_value.getType() != ValueType.STRING) {
            throw new RuntimeException("//#outname needs a string expression");
        }
        state.getFileReference().setDestinationName((String) p_value.getValue());
        return DirectiveBehaviourEnum.PROCESSED;
    }
}
