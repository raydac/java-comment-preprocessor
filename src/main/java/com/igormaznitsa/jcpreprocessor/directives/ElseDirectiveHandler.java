package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import java.io.IOException;

public class ElseDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "else";
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
        if (state.isIfCounterZero()) {
            throw new IOException("You have got an #else instruction without #if");
        }

        if (state.getIfCounter() == state.getActiveIfCounter()) {
            state.setIfEnabled(!state.isIfEnabled());
        }
        return DirectiveBehaviour.NORMAL;
    }
}
