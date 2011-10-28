package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
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
        return "it continues current "+DIRECTIVE_PREFIX+"while iteration";
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext configurator) {
        if (state.isWhileStackEmpty()) {
            throw new RuntimeException(getFullName()+" without "+DIRECTIVE_PREFIX+"while detected");
        }
        
        final TextFileDataContainer whileContainer = state.peekWhile();
        state.popAllIfUntil(whileContainer);
        state.popWhile();
        state.goToString(whileContainer.getNextStringIndex());
        return AfterProcessingBehaviour.PROCESSED;
    }
}
