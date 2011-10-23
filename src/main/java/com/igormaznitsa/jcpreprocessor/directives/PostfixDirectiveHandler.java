package com.igormaznitsa.jcpreprocessor.directives;

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
    public DirectiveBehaviourEnum execute(final String string, final ParameterContainer state, final PreprocessorContext configurator) {
        if (!string.isEmpty()) {
            switch (string.charAt(0)) {
                case '+': {
                    state.setCurrentOutStream(state.getPostfixOutStream());
                }
                break;
                case '-': {
                    state.setCurrentOutStream(state.getNormalOutStream());
                }
                break;
                default:
                    throw new IllegalArgumentException("Unsupported paremeter");
            }
            return DirectiveBehaviourEnum.READ_NEXT_LINE;
        }
        throw new RuntimeException("//#prefix needs a parameter");
    }
}
