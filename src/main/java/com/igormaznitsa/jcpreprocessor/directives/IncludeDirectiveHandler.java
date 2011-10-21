package com.igormaznitsa.jcpreprocessor.directives;

public class IncludeDirectiveHandler  extends DirectiveHandler {

    @Override
    public String getName() {
        return "include";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
