package com.igormaznitsa.jcpreprocessor.directives;

public class IfDirectiveHandler  extends DirectiveHandler  {

    @Override
    public String getName() {
        return "if";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
