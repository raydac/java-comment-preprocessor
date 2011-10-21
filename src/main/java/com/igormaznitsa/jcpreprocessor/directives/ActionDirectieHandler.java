package com.igormaznitsa.jcpreprocessor.directives;

public class ActionDirectieHandler extends DirectiveHandler {

    @Override
    public String getName() {
        return "action";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
