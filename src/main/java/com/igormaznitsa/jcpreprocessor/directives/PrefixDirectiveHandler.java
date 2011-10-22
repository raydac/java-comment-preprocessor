package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import java.io.IOException;

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
    public DirectiveBehaviour execute(final String string, final ParameterContainer state, final PreprocessorContext configurator) throws IOException {
        if (!string.isEmpty()) {
            switch (string.charAt(0)) {
                case '+': {
                    state.setCurrentOutStream(state.getPrefixOutStream());
                }
                break;
                case '-': {
                    state.setCurrentOutStream(state.getNormalOutStream());
                }
                break;
                default:
                    throw new IllegalArgumentException("Unsupported char");
            }
            return DirectiveBehaviour.CONTINUE;
        }
        throw new RuntimeException("Empty string");
    }
}
