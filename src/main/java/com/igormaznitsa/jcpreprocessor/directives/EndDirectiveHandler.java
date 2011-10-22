package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
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
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext configurator) throws IOException {
        if (state.isWhileCounterZero()) {
            throw new IOException("You have got an #end instruction without #while");
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
        return DirectiveBehaviour.NORMAL;
    }
}
