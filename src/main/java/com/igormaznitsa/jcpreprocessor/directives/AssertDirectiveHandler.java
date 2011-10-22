package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import java.io.IOException;

public class AssertDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "assert";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext configurator) throws IOException {
        if (state.isOutEnabled()) {
            configurator.info("-->: " + string.trim());
        }
        return DirectiveBehaviour.NORMAL;
    }
}
