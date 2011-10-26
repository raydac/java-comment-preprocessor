package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class IfDefinedDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "ifdefined";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getReference() {
        return null;
    }

    @Override
    public boolean processOnlyIfCanBeProcessed() {
        return false;
    }

    
    @Override
    public DirectiveBehaviour execute(final String string, final ParameterContainer state, final PreprocessorContext configurator) {
        if (state.isDirectiveCanBeProcessed()){
            if (string.isEmpty()) {
                throw new RuntimeException("//#ifdefined needs a variable");
            }
            state.pushIf(true);
            final boolean definitionFlag = configurator.findVariableForName(string) != null;
            if (!definitionFlag){
                state.getState().add(PreprocessingState.IF_CONDITION_FALSE);
            }
        }else{
            state.pushIf(false);
        }
 
        return DirectiveBehaviour.PROCESSED;
    }
}
