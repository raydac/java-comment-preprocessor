package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class CommentNextLineDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "//";
    }

    @Override
    public boolean hasExpression() {
        return false;
    }

    @Override
    public String getReference() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public DirectiveBehaviour execute(final String string, final ParameterContainer state, final PreprocessorContext configurator) {
         state.getState().add(PreprocessingState.COMMENT_NEXT_LINE);
         return DirectiveBehaviour.PROCESSED;
    }
 
    
}
