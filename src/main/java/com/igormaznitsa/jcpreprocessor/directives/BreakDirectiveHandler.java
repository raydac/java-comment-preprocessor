package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import java.io.IOException;

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
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext configurator) throws IOException {
        if (state.isWhileCounterZero()) {
            throw new IOException("You have #break without #when");
        }

        if (state.isProcessingEnabled() && state.getWhileCounter() == state.getActiveWhileCounter()) {
            state.setThereIsNoBreakCommand(false);
        }
        return DirectiveBehaviour.NORMAL;
    }

    @Override
    public boolean processOnlyIfProcessingEnabled() {
        return false;
    }
}
