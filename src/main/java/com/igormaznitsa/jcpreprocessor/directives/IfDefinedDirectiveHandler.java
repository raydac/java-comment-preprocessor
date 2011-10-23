package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import java.io.IOException;

public class IfDefinedDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "ifdefined";
    }

    @Override
    public boolean hasExpression() {
        return true;
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
        // Processing #ifdefine instruction
        if (state.isProcessingEnabled()) {
            if (string.isEmpty()) {
                throw new RuntimeException("//#ifdefined needs a variable");
            }

            boolean lg_defined = configurator.findVariableForName(string) != null;

            if (state.isIfCounterZero()) {
                state.setLastIfFileName(state.getCurrentFileCanonicalPath());
                state.setLastIfStringNumber(state.getCurrentStringIndex());
            }
            state.increaseIfCounter();
            state.setActiveIfCounter(state.getIfCounter());

            if (lg_defined) {
                state.setIfEnabled(true);
            } else {
                state.setIfEnabled(false);
            }
        } else {
            state.increaseIfCounter();
        }

        return DirectiveBehaviourEnum.PROCESSED;
    }
}
