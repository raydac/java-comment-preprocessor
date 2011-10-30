package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class GlobalVariableHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/P:";
    
    public String getDescription() {
        return "set a global variable, for instance /P:DEBUG=true";
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
        if (argument.toUpperCase().startsWith(ARG_NAME)){
            configurator.addGlobalVariable(PreprocessorUtils.extractTail(ARG_NAME, argument),null);
            return true;
        } else {
            return false;
        }
    }

    public String getKeyName() {
        return ARG_NAME;
    }
    
}
