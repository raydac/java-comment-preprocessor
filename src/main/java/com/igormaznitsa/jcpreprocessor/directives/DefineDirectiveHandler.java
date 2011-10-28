package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;

public class DefineDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "define";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getExpressionType() {
        return "VAR_NAME";
    }
    
    @Override
    public String getReference() {
        return "it defines a local variable for the name and init it as TRUE, the name must be undefined before";
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context) {
        if (context.findVariableForName(string)!=null){
            throw new RuntimeException("variable already defined");
        }
        context.setLocalVariable(string, Value.BOOLEAN_TRUE);
        return AfterProcessingBehaviour.PROCESSED;
    }

}
