package com.igormaznitsa.jcpreprocessor.directives;

public class BreakDirectiveHandler  extends DirectiveHandler  {

    @Override
    public String getName() {
        return "break";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
