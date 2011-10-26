package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
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
    public DirectiveBehaviour execute(final String string, final ParameterContainer state, final PreprocessorContext configurator) {
        if (!string.isEmpty()) {
            switch (string.charAt(0)) {
                case '+': {
                    state.setPrinter(ParameterContainer.PrinterType.PREFIX);
                }
                break;
                case '-': {
                    state.setPrinter(ParameterContainer.PrinterType.NORMAL);
                }
                break;
                default:
                    throw new IllegalArgumentException("Unsupported parameter");
            }
            return DirectiveBehaviour.PROCESSED;
        }
        throw new RuntimeException("//#prefix needs a parameter");
    }
}
