package com.igormaznitsa.jcpreprocessor.cmd;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;

public class RemoveCommentsHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/R";
    
    public String getDescription() {
        return "after preprocessing the new file will be completely cleared of comments in Java-C style";
    }

    public boolean processArgument(final String argument, final Configurator configurator) {
        if (ARG_NAME.equalsIgnoreCase(argument)){
            configurator.setRemovingComments(true);
            return true;
        } else {
            return false;
        }
    }

    public String getKeyName() {
        return ARG_NAME;
    }
    
}
