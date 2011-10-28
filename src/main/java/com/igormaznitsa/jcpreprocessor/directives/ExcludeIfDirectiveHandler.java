package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.IOException;

public class ExcludeIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "excludeif";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getReference() {
        return "it excludes the file from the preprocessing list if the expression is true";
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
    public String getExpressionType() {
        return "BOOLEAN";
    }
    
    @Override
    public AfterProcessingBehaviour execute(String string, PreprocessingState state, PreprocessorContext context) {
        state.pushExcludeIfData(state.getRootFileInfo(), string, state.peekFile().getNextStringIndex()-1);
        return AfterProcessingBehaviour.PROCESSED;
    }
}
