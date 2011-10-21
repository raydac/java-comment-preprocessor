package com.igormaznitsa.jcpreprocessor.directives;

public class AssertDirectiveHandler  extends DirectiveHandler {

    @Override
    public String getName() {
        return "assert";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
