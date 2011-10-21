package com.igormaznitsa.jcpreprocessor.directives;

public class DefineDirectiveHandler  extends DirectiveHandler {

    @Override
    public String getName() {
        return "define";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
