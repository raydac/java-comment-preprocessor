package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class GlobalElseDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "_else";
    }

    @Override
    public String getReference() {
        return "it inverts the conditional flag for the current global //#_if..//#_else..//#_endif construction";
    }

    @Override
    public boolean isGlobalPhaseAllowed() {
        return true;
    }

    @Override
    public boolean isPreprocessingPhaseAllowed() {
        return false;
    }
    
    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext configurator) {
        if (state.isIfStackEmpty()) {
            throw new RuntimeException(DIRECTIVE_PREFIX+"_else without "+DIRECTIVE_PREFIX+"_if detected");
        }

        if (state.isAtActiveIf()) {
            if (state.getPreprocessingFlags().contains(PreprocessingFlag.IF_CONDITION_FALSE)){
                state.getPreprocessingFlags().remove(PreprocessingFlag.IF_CONDITION_FALSE);
            } else {
                state.getPreprocessingFlags().add(PreprocessingFlag.IF_CONDITION_FALSE);
            }
        }
        return AfterProcessingBehaviour.PROCESSED;
    }
    
    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

}
