package com.igormaznitsa.jcpreprocessor.directives;

public class ContinueDirectiveHandler  extends DirectiveHandler  {

    @Override
    public String getName() {
        return "continue";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
