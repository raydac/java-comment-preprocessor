package com.igormaznitsa.jcpreprocessor.directives;

public class FlushDirectiveHandler  extends DirectiveHandler {

    @Override
    public String getName() {
        return "flush";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
