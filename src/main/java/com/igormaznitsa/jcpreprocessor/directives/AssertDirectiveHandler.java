package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class AssertDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "assert";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getExpressionType() {
        return "STRING";
    }

    @Override
    public String getReference() {
        return "it prints an info at the console, use macroses to print variables";
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext configurator) {
        configurator.info("--> " + string.trim());
        return AfterProcessingBehaviour.PROCESSED;
    }
}
