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
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getReference() {
        return null;
    }

    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

    @Override
    public String getExpressionType() {
        return "VAR_NAME";
    }
    
    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext configurator) {
        if (state.isDirectiveCanBeProcessed()){
            if (string.isEmpty()) {
                throw new RuntimeException(DIRECTIVE_PREFIX+"ifdefined needs a variable");
            }
            state.pushIf(true);
            final boolean definitionFlag = configurator.findVariableForName(string) != null;
            if (!definitionFlag){
                state.getPreprocessingFlags().add(PreprocessingFlag.IF_CONDITION_FALSE);
            }
        }else{
            state.pushIf(false);
        }
 
        return AfterProcessingBehaviour.PROCESSED;
    }
}
