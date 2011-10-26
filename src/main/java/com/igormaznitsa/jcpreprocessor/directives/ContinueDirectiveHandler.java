package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.containers.TextFileDataContainer;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class ContinueDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "continue";
    }

    @Override
    public boolean hasExpression() {
        return false;
    }

    @Override
    public String getReference() {
        return "it continues current //#while iteration";
    }

    @Override
    public DirectiveBehaviour execute(final String string, final ParameterContainer state, final PreprocessorContext configurator) {
        if (state.isWhileStackEmpty()) {
            throw new RuntimeException("#continue without #while detected");
        }
        
        final TextFileDataContainer whileContainer = state.peekWhile();
        state.popAllIfUntil(whileContainer);
        state.popWhile();
        state.goToString(whileContainer.getNextStringIndex());
        return DirectiveBehaviour.PROCESSED;
    }
}
