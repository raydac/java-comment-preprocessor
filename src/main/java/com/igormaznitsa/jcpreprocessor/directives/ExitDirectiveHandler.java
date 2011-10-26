package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
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
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext configurator) {
        state.getState().add(PreprocessingState.END_PROCESSING);
        return DirectiveBehaviour.READ_NEXT_LINE;
    }
}
