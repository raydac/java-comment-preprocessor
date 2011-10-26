package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

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
    public DirectiveBehaviour execute(final String string, final ParameterContainer state, final PreprocessorContext configurator) {
        state.getState().remove(PreprocessingState.TEXT_OUTPUT_DISABLED);
        return DirectiveBehaviour.PROCESSED;
    }
    
    
}
