package com.igormaznitsa.jcpreprocessor.directives;

public class WhileDirectiveHandler  extends DirectiveHandler {

    @Override
    public String getName() {
        return "while";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
