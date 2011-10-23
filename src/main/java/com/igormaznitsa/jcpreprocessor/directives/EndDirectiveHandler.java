package com.igormaznitsa.jcpreprocessor.directives;

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
    public boolean processOnlyIfProcessingEnabled() {
        return false;
    }

    @Override
    public String getReference() {
        return null;
    }

    @Override
    public DirectiveBehaviourEnum execute(String string, ParameterContainer state, PreprocessorContext configurator) {
        if (state.isWhileCounterZero()) {
            throw new RuntimeException("//#end without //#while detected");
        }

        int i_lastWhileIndex = state.popWhileIndex();

        if (state.getWhileCounter() == state.getActiveWhileCounter()) {
            state.decreaseWhileCounter();
            state.decreaseActiveWhileCounter();

            if (state.isThereNoBreakCommand()) {
                state.setCurrentStringIndex(i_lastWhileIndex);
            }

            state.setThereIsNoContinueCommand(true);
            state.setThereIsNoBreakCommand(true);
        } else {
            state.decreaseWhileCounter();
        }
        return DirectiveBehaviourEnum.PROCESSED;
    }
}
