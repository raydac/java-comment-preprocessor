package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import java.io.IOException;

public class ExitDirectiveHandler  extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public boolean hasExpression() {
        return false;
    }

    @Override
    public String getReference() {
        return null;
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context) {
        state.getPreprocessingFlags().add(PreprocessingFlag.END_PROCESSING);
        return AfterProcessingBehaviour.READ_NEXT_LINE;
    }
}
