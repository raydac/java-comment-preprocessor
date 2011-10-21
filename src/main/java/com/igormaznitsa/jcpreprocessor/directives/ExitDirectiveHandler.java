package com.igormaznitsa.jcpreprocessor.directives;

public class ExitDirectiveHandler  extends DirectiveHandler {

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
