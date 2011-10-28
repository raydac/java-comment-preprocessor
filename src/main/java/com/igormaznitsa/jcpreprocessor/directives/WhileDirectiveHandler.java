package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

public class WhileDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "while";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getReference() {
        return "it makes a loop until "+DIRECTIVE_PREFIX+"end if the condition is true";
    }

    @Override
    public boolean executeOnlyWhenExecutionAllowed() {
        return false;
    }

    @Override
    public String getExpressionType() {
        return "BOOLEAN";
    }
    
    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context) {
        if (state.isDirectiveCanBeProcessed()) {
            final Value condition = Expression.eval(string,context);
            if (condition == null || condition.getType() != ValueType.BOOLEAN) {
                throw new RuntimeException(DIRECTIVE_PREFIX+"while needs a boolean expression");
            }

            state.pushWhile(true);
            if (!condition.asBoolean().booleanValue())
            {
                state.getPreprocessingFlags().add(PreprocessingFlag.BREAK_COMMAND);
            }
        } else {
           state.pushWhile(false);
        }
        
        return AfterProcessingBehaviour.PROCESSED;
    }
}
