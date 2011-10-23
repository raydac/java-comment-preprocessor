package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import java.io.IOException;

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
    public DirectiveBehaviourEnum execute(String string, ParameterContainer state, PreprocessorContext configurator) {
         state.setCommentNextLine(true);
         return DirectiveBehaviourEnum.PROCESSED;
    }
 
    
}
