package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;

public class ClearDstDirectoryHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/C";
    
    public String getDescription() {
        return "the destination directory will be cleared before processing";
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
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
