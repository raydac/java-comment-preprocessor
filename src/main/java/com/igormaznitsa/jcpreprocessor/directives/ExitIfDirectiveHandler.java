package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.IOException;

public class ExitIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "exitif";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getReference() {
        return "it interrupts the preprocessing if the excpression is true";
    }

    @Override
    public String getExpressionType() {
        return "BOOLEAN";
    }
    
    @Override
    public AfterProcessingBehaviour execute(String string, PreprocessingState state, PreprocessorContext context) {
        // To end processing the file processing immediatly if the value is true
        final Value condition = Expression.eval(string,context);
        if (condition == null || condition.getType() != ValueType.BOOLEAN) {
            throw new RuntimeException(DIRECTIVE_PREFIX+"exitif needs a boolean condition");
        }
        if (((Boolean) condition.getValue()).booleanValue()) {
            state.getPreprocessingFlags().add(PreprocessingFlag.END_PROCESSING);
            return AfterProcessingBehaviour.READ_NEXT_LINE;
        }
        return AfterProcessingBehaviour.PROCESSED;
    }

 }
