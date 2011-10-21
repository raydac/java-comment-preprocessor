package com.igormaznitsa.jcpreprocessor.directives;

public class ExitIfDirectiveHandler  extends DirectiveHandler {

    @Override
    public String getName() {
        return "exitif";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
