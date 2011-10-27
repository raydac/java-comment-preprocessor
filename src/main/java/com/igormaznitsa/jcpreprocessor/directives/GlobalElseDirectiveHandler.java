package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class GlobalElseDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "_else";
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
    public boolean isFirstPassAllowed() {
        return true;
    }

    @Override
    public boolean isSecondPassAllowed() {
        return false;
    }
    
    @Override
    public DirectiveBehaviour execute(final String string, final ParameterContainer state, final PreprocessorContext configurator) {
        if (state.isIfStackEmpty()) {
            throw new RuntimeException(DIRECTIVE_PREFIX+"_else without "+DIRECTIVE_PREFIX+"_if detected");
        }

        if (state.isAtActiveIf()) {
            if (state.getState().contains(PreprocessingState.IF_CONDITION_FALSE)){
                state.getState().remove(PreprocessingState.IF_CONDITION_FALSE);
            } else {
                state.getState().add(PreprocessingState.IF_CONDITION_FALSE);
            }
        }
        return DirectiveBehaviour.PROCESSED;
    }
    
    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

}
