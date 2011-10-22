package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
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
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext configurator) throws IOException {
                    if (state.isIfCounterZero()) {
                        throw new IOException("You have got an #endif instruction without #if");
                    }
                    
                    if (state.getIfCounter() == state.getActiveIfCounter()) {
                        state.decreaseIfCounter();
                        state.decreaseActiveIfCounter();
                        state.setIfEnabled(true);
                    } else {
                        state.decreaseIfCounter();
                    }
        return DirectiveBehaviour.NORMAL;
    }
    
    
}
