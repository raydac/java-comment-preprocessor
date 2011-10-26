package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

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
    public DirectiveBehaviour execute(final String string, final ParameterContainer state, final PreprocessorContext context) {
        final Value dirName = Expression.eval(string, context);

        if (dirName == null || dirName.getType() != ValueType.STRING) {
            throw new RuntimeException("//#outname needs a string expression");
        }
        state.getRootFileInfo().setDestinationName(dirName.asString());
        return DirectiveBehaviour.PROCESSED;
    }
}
