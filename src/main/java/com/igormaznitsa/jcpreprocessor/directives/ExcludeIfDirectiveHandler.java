package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.IOException;

public class ExcludeIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "excludeif";
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
    public boolean executeDuringGlobalPass() {
        return true;
    }

    @Override
    public boolean executeDuringLocalPass() {
        return false;
    }

    @Override
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext context) {
        final Value flag = Expression.eval(string, context);

        if (flag == null || flag.getType() != ValueType.BOOLEAN) {
            throw new RuntimeException(DIRECTIVE_PREFIX + "excludeif needs a boolean expression");
        }

        if (flag.asBoolean().booleanValue()) {
            state.getRootFileInfo().setExcluded(true);
        }
        return DirectiveBehaviour.PROCESSED;
    }
}
