package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingFlag;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

public class ExitIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "exitif";
    }

    @Override
    public String getReference() {
        return "it interrupts the preprocessing if the excpression is true";
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.BOOLEAN;
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context) {
        // To end processing the file processing immediatly if the value is true
        final Value condition = Expression.evalExpression(string,context,state);
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
