package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;

public class VerboseHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/V";
    
    public String getDescription() {
        return "make the preprocessor to out additional info about its operations";
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
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
