package com.igormaznitsa.jcpreprocessor.directives;

public class PrefixDirectiveHandler  extends DirectiveHandler  {

    @Override
    public String getName() {
        return "prefix";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return false;
    }
    
}
