package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class BreakDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "break";
    }

    @Override
    public boolean hasExpression() {
        return false;
    }

    @Override
    public String getReference() {
        return "it allows to break the current //#while...//#end construction";
    }

    @Override
    public DirectiveBehaviour execute(final String string, final ParameterContainer state, final PreprocessorContext context) {
        if (state.isWhileStackEmpty()) {
            throw new RuntimeException("//#break without //#while detected");
        }

        state.getState().add(PreprocessingState.BREAK_COMMAND);
        return DirectiveBehaviour.PROCESSED;
    }
}
