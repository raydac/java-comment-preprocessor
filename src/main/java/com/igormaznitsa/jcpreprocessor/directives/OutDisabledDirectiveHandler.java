package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class OutDisabledDirectiveHandler  extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "-";
    }

    @Override
    public String getReference() {
        return "it allows to switch off text output process";
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.NONE;
    }
    
    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext configurator) {
        state.getPreprocessingFlags().add(PreprocessingFlag.TEXT_OUTPUT_DISABLED);
        return AfterProcessingBehaviour.PROCESSED;
    }
    
    
}
