package com.igormaznitsa.jcpreprocessor.cmd;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;

public class ProcessorClearDstDirectory implements CommandLineArgumentProcessor {

    private static final String ARG_NAME = "/C";
    
    public String getDescription() {
        return "the destination directory will be cleared before processing";
    }

    public boolean processArgument(final String argument, final Configurator configurator) {
        if (ARG_NAME.equalsIgnoreCase(argument)){
            configurator.setClearDestinationDirBefore(true);
            return true;
        } else {
            return false;
        }
    }

    public String getKeyName() {
       return ARG_NAME;
    }
    
}
