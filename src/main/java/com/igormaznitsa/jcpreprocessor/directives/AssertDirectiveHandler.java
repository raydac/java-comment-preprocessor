package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class AssertDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "assert";
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.STRING;
    }

    @Override
    public String getReference() {
        return "it prints an info at the console, use macroses to print variables";
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext configurator) {
        configurator.info("--> " + PreprocessorUtils.processMacroses(string.trim(), configurator, state));
        return AfterProcessingBehaviour.PROCESSED;
    }
}
