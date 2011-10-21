package com.igormaznitsa.jcpreprocessor.directives;

public class EndIfDirectiveHandler  extends DirectiveHandler {

    @Override
    public String getName() {
        return "endif";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
