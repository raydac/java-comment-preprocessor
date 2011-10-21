package com.igormaznitsa.jcpreprocessor.directives;

public class OutDirDirectiveHandler extends DirectiveHandler  {

    @Override
    public String getName() {
        return "outdir";
    }

    @Override
    public boolean hasSpaceOrEndAfter() {
        return true;
    }
    
}
