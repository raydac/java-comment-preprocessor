package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class EndIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "endif";
    }

    @Override
    public boolean hasExpression() {
        return false;
    }

    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

    @Override
    public String getReference() {
        return "it is the end part of a "+DIRECTIVE_PREFIX+"if..."+DIRECTIVE_PREFIX+"endif structure";
    }

    @Override
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext configurator) {
        if (state.isIfStackEmpty()) {
            throw new RuntimeException(DIRECTIVE_PREFIX+"endif without "+DIRECTIVE_PREFIX+"if detected");
        }

        if (!state.isDirectiveCanBeProcessed() && state.isAtActiveIf()) {
            state.getState().remove(PreprocessingState.IF_CONDITION_FALSE);
        } 
        
        state.popIf();
        
        return DirectiveBehaviour.PROCESSED;
    }
}
