package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

public class IfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "if";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getReference() {
        return "allows to make "+DIRECTIVE_PREFIX+"if.."+DIRECTIVE_PREFIX+"else.."+DIRECTIVE_PREFIX+"endif construction, needs a boolean expression as the argument";
    }

    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

    @Override
    public DirectiveBehaviour execute(final String string, final ParameterContainer state, final PreprocessorContext context) {
        if (state.isDirectiveCanBeProcessed()){
            final Value expressionResult = Expression.eval(string,context);
            if (expressionResult == null || expressionResult.getType() != ValueType.BOOLEAN) {
                throw new RuntimeException(DIRECTIVE_PREFIX+"if needs a boolean expression");
            }
            state.pushIf(true);
            if (!expressionResult.asBoolean().booleanValue()){
                state.getState().add(PreprocessingState.IF_CONDITION_FALSE);
            }
        }else{
            state.pushIf(false);
        }
 
        return DirectiveBehaviour.PROCESSED;
    }
}
