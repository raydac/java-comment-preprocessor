package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class ElseDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "else";
    }

    @Override
    public String getReference() {
        return "a part of a "+DIRECTIVE_PREFIX+"if.."+DIRECTIVE_PREFIX+"endif structure, it inverts condition flag";
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext configurator) {
        if (state.isIfStackEmpty()) {
            throw new RuntimeException(getFullName()+" without "+DIRECTIVE_PREFIX+"if detected");
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
