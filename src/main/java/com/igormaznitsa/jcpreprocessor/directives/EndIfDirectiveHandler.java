package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class EndIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "endif";
    }

    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

    @Override
    public String getReference() {
        return "it is the end part of a "+DIRECTIVE_PREFIX+"if..."+DIRECTIVE_PREFIX+"endif structure";
    }

    @Override
    public AfterProcessingBehaviour execute(String string, PreprocessingState state, PreprocessorContext configurator) {
        if (state.isIfStackEmpty()) {
            throw new RuntimeException(getFullName()+" without "+DIRECTIVE_PREFIX+"if detected");
        }

        if (!state.isDirectiveCanBeProcessed() && state.isAtActiveIf()) {
            state.getPreprocessingFlags().remove(PreprocessingFlag.IF_CONDITION_FALSE);
        } 
        
        state.popIf();
        
        return AfterProcessingBehaviour.PROCESSED;
    }
}
