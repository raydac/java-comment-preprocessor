package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
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
    public String getReference() {
        return null;
    }

    @Override
    public DirectiveBehaviourEnum execute(String string, ParameterContainer state, PreprocessorContext configurator) {
        if (state.isIfCounterZero()) {
            throw new RuntimeException("//#else without //#if detected");
        }

        if (state.getIfCounter() == state.getActiveIfCounter()) {
            state.setIfEnabled(!state.isIfEnabled());
        }
        return DirectiveBehaviourEnum.PROCESSED;
    }
}
