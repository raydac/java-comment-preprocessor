package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.IOException;

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
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext configurator) throws IOException {
        configurator.setLocalVariable(string, Value.BOOLEAN_TRUE);
        return DirectiveBehaviour.NORMAL;
    }

}
