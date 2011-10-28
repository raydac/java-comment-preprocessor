package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class PrefixDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "prefix";
    }

    @Override
    public boolean hasExpression() {
        return false;
    }

    @Override
    public String getReference() {
        return null;
    }

    @Override
    public String getExpressionType() {
        return "[+|-]";
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
