package com.igormaznitsa.jcpreprocessor.directives;

public class CommentNextLineDirectiveHandler extends DirectiveHandler {

    @Override
    public String getName() {
        return "//";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
