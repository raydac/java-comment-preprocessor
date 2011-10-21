package com.igormaznitsa.jcpreprocessor.directives;

public class OutNameDirectiveHandler  extends DirectiveHandler {

    @Override
    public String getName() {
        return "outname";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
