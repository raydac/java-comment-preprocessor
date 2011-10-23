package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
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
    public String getReference() {
        return null;
    }

    @Override
    public DirectiveBehaviourEnum execute(String string, ParameterContainer state, PreprocessorContext configurator) {
        configurator.setLocalVariable(string, Value.BOOLEAN_TRUE);
        return DirectiveBehaviourEnum.PROCESSED;
    }

}
