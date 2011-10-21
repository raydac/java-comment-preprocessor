package com.igormaznitsa.jcpreprocessor.directives;

public class EndDirectiveHandler  extends DirectiveHandler {

    @Override
    public String getName() {
        return "end";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
