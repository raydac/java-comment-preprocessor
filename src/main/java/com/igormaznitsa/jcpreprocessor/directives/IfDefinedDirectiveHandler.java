package com.igormaznitsa.jcpreprocessor.directives;

public class IfDefinedDirectiveHandler  extends DirectiveHandler {

    @Override
    public String getName() {
        return "ifdefined";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
