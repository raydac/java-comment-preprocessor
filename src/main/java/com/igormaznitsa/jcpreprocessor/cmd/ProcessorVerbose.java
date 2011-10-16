package com.igormaznitsa.jcpreprocessor.cmd;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;

public class ProcessorVerbose implements CommandLineArgumentProcessor {

    private static final String ARG_NAME = "/V";
    
    public String getDescription() {
        return "make the preprocessor to out additional info about its operations";
    }

    public boolean processArgument(final String argument, final Configurator configurator) {
        if (ARG_NAME.equalsIgnoreCase(argument)){
            configurator.setVerbose(true);
            return true;
        } else {
            return false;
        }
    }

    public String getKeyName() {
        return ARG_NAME;
    }
    
}
