package com.igormaznitsa.jcpreprocessor.directives;

public class ElseDirectiveHandler  extends DirectiveHandler {

    @Override
    public String getName() {
        return "else";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
