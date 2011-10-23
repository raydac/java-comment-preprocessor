package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import java.io.IOException;

public class OutEnabledDirectiveHandler  extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "+";
    }

    @Override
    public boolean hasExpression() {
        return false;
    }

    @Override
    public String getReference() {
        return null;
    }

    @Override
    public DirectiveBehaviourEnum execute(String string, ParameterContainer state, PreprocessorContext configurator) {
        state.setOutEnabled(true);
        return DirectiveBehaviourEnum.PROCESSED;
    }
    
    
}
