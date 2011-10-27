package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class GlobalEndIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "_endif";
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
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext configurator) {
        if (state.isIfStackEmpty()) {
            throw new RuntimeException(DIRECTIVE_PREFIX+"_endif without "+DIRECTIVE_PREFIX+"_if detected");
        }

        if (!state.isDirectiveCanBeProcessed() && state.isAtActiveIf()) {
            state.getState().remove(PreprocessingState.IF_CONDITION_FALSE);
        } 
        
        state.popIf();
        
        return DirectiveBehaviour.PROCESSED;
    }
}
