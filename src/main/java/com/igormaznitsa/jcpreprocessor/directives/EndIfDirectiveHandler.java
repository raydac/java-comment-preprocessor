package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import java.io.IOException;

public class EndIfDirectiveHandler  extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "endif";
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
                        throw new RuntimeException("//#endif without //#if detected");
                    }
                    
                    if (state.getIfCounter() == state.getActiveIfCounter()) {
                        state.decreaseIfCounter();
                        state.decreaseActiveIfCounter();
                        state.setIfEnabled(true);
                    } else {
                        state.decreaseIfCounter();
                    }
        return DirectiveBehaviourEnum.PROCESSED;
    }
    
    
}
