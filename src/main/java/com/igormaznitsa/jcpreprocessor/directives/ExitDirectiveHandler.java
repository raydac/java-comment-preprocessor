package com.igormaznitsa.jcpreprocessor.directives;

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
    public DirectiveBehaviourEnum execute(String string, ParameterContainer state, PreprocessorContext configurator) {
        state.setEndPreprocessing(true);
        return DirectiveBehaviourEnum.READ_NEXT_LINE;
    }
    
    
}
