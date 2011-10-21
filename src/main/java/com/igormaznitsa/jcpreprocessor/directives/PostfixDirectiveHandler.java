package com.igormaznitsa.jcpreprocessor.directives;

public class PostfixDirectiveHandler  extends DirectiveHandler  {

    @Override
    public String getName() {
        return "postfix";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return false;
    }
    
}
