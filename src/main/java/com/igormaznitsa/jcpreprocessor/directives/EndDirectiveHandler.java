package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.containers.TextFileDataContainer;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class EndDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "end";
    }

    @Override
    public String getReference() {
        return "it ends a "+DIRECTIVE_PREFIX+"while..."+DIRECTIVE_PREFIX+"end construction";
    }

    @Override
    public AfterProcessingBehaviour execute(String string, PreprocessingState state, PreprocessorContext configurator) {
        if (state.isWhileStackEmpty()) {
            throw new RuntimeException(DIRECTIVE_PREFIX+"end without "+DIRECTIVE_PREFIX+"while detected");
        }

        if (state.isDirectiveCanBeProcessedIgnoreBreak()) {
            final TextFileDataContainer thisWhile = state.peekWhile();
            final boolean breakIsSet = state.getPreprocessingFlags().contains(PreprocessingFlag.BREAK_COMMAND);
            state.popWhile();
            if (!breakIsSet) {
                state.goToString(thisWhile.getNextStringIndex());
            }
        } else {
            state.popWhile();
        }
        return AfterProcessingBehaviour.PROCESSED;
    }

    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

}
