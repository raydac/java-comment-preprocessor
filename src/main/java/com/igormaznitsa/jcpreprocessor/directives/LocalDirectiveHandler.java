package com.igormaznitsa.jcpreprocessor.directives;

public class LocalDirectiveHandler  extends DirectiveHandler {

    @Override
    public String getName() {
        return "local";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
