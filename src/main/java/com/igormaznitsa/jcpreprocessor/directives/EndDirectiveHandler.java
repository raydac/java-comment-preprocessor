package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.containers.TextFileDataContainer;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import java.io.IOException;

public class EndDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "end";
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
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext configurator) {
        if (state.isWhileStackEmpty()) {
            throw new RuntimeException("//#end without //#while detected");
        }

        if (state.isDirectiveCanBeProcessedIgnoreBreak()) {
            final TextFileDataContainer thisWhile = state.peekWhile();
            final boolean breakIsSet = state.getState().contains(PreprocessingState.BREAK_COMMAND);
            state.popWhile();
            if (!breakIsSet) {
                state.goToString(thisWhile.getNextStringIndex());
            }
        } else {
            state.popWhile();
        }
        return DirectiveBehaviour.PROCESSED;
    }

    @Override
    public boolean processOnlyIfCanBeProcessed() {
        return false;
    }

}
