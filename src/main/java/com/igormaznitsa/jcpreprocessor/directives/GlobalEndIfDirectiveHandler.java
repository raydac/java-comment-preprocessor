package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class GlobalEndIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "_endif";
    }

    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

    @Override
    public String getReference() {
        return "it ends the current global //#_if..//#_else..//#_endif construction";
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
    public AfterProcessingBehaviour execute(String string, PreprocessingState state, PreprocessorContext configurator) {
        if (state.isIfStackEmpty()) {
            throw new RuntimeException(DIRECTIVE_PREFIX+"_endif without "+DIRECTIVE_PREFIX+"_if detected");
        }

        if (!state.isDirectiveCanBeProcessed() && state.isAtActiveIf()) {
            state.getPreprocessingFlags().remove(PreprocessingFlag.IF_CONDITION_FALSE);
        } 
        
        state.popIf();
        
        return AfterProcessingBehaviour.PROCESSED;
    }
}
