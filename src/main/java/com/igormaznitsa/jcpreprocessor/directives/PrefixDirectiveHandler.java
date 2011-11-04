package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class PrefixDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "prefix";
    }

    @Override
    public String getReference() {
        return "it allows either to switch on (+) or switch off (-) the mode when all texts are printed into the prefix part";
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.ONOFF;
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext configurator) {
        if (!string.isEmpty()) {
            switch (string.charAt(0)) {
                case '+': {
                    state.setPrinter(PreprocessingState.PrinterType.PREFIX);
                }
                break;
                case '-': {
                    state.setPrinter(PreprocessingState.PrinterType.NORMAL);
                }
                break;
                default:
                    throw new IllegalArgumentException("Unsupported parameter");
            }
            return AfterProcessingBehaviour.PROCESSED;
        }
        throw new RuntimeException(DIRECTIVE_PREFIX+"prefix needs a parameter");
    }
}
