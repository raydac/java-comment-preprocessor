package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class PostfixDirectiveHandler  extends AbstractDirectiveHandler  {

    @Override
    public String getName() {
        return "postfix";
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
                    state.setPrinter(PreprocessingState.PrinterType.POSTFIX);
                }
                break;
                case '-': {
                    state.setPrinter(PreprocessingState.PrinterType.NORMAL);
                }
                break;
                default:
                    throw new IllegalArgumentException("Unsupported paremeter");
            }
            return AfterProcessingBehaviour.PROCESSED;
        }
        throw new RuntimeException(DIRECTIVE_PREFIX+"prefix needs a parameter");
    }
}
