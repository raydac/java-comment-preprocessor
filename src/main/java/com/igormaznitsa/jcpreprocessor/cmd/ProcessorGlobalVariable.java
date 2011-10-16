package com.igormaznitsa.jcpreprocessor.cmd;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class ProcessorGlobalVariable implements CommandLineArgumentProcessor {

    private static final String ARG_NAME = "/P:";
    
    public String getDescription() {
        return "set a global variable, for instance /P:DEBUG=true";
    }

    public boolean processArgument(final String argument, final Configurator configurator) {
        if (argument.toUpperCase().startsWith(ARG_NAME)){
            configurator.addGlobalVariable(PreprocessorUtils.extractTail(ARG_NAME, argument));
            return true;
        } else {
            return false;
        }
    }

    public String getKeyName() {
        return ARG_NAME;
    }
    
}
