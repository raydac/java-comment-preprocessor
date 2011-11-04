package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class IfDefinedDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "ifdefined";
    }

    @Override
    public String getReference() {
        return "it takes a variable name and work like //#if if the variable has been defined in the scope";
    }

    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.VARNAME;
    }
    
    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext configurator) {
        if (state.isDirectiveCanBeProcessed()){
            if (string.isEmpty()) {
                throw new RuntimeException(DIRECTIVE_PREFIX+"ifdefined needs a variable");
            }
            state.pushIf(true);
            final boolean definitionFlag = configurator.findVariableForName(string,state) != null;
            if (!definitionFlag){
                state.getPreprocessingFlags().add(PreprocessingFlag.IF_CONDITION_FALSE);
            }
        }else{
            state.pushIf(false);
        }
 
        return AfterProcessingBehaviour.PROCESSED;
    }
}
