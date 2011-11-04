package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class GlobalDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "global";
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.SET;
    }
    
    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context){
        processLocalDefinition(string, context ,state);
        return AfterProcessingBehaviour.PROCESSED;
    }

    @Override
    public String getReference() {
        return "it allows to set a global variable value";
    }

    @Override
    public boolean isGlobalPhaseAllowed() {
        return true;
    }

    @Override
    public boolean isPreprocessingPhaseAllowed() {
        return false;
    }
    
    private void processLocalDefinition(final String string, final PreprocessorContext context, final PreprocessingState state) {
        final String[] splitted = PreprocessorUtils.splitForSetOperator(string);

        if (splitted.length != 2) {
            throw new RuntimeException("Can't recognize the expression");
        }

        final String name = splitted[0].trim();
        final Value value = Expression.eval(splitted[1].trim(), context,state);

        if (value == null) {
            throw new RuntimeException("Unsupported expression result");
        }

        context.setGlobalVariable(name,value,state);

        if (context.isVerbose()){
            if (context.containsGlobalVariable(name)){
                context.warning("Global value has been changed ["+name+'='+value+']');
            }
        }
        
    }
}
