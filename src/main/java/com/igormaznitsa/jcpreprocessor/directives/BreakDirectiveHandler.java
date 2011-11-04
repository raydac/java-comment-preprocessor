package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class BreakDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "break";
    }

    @Override
    public String getReference() {
        return "it allows to break the current "+DIRECTIVE_PREFIX+"while..."+DIRECTIVE_PREFIX+"end construction";
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context) {
        if (state.isWhileStackEmpty()) {
            throw new RuntimeException(getFullName()+" without "+DIRECTIVE_PREFIX+"while detected");
        }

        state.getPreprocessingFlags().add(PreprocessingFlag.BREAK_COMMAND);
        return AfterProcessingBehaviour.PROCESSED;
    }
}
