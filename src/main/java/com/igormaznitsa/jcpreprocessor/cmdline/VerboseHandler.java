package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class VerboseHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/V";
    
    public String getDescription() {
        return "switch on the verbose level";
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
        boolean result = false;
        
        if (ARG_NAME.equalsIgnoreCase(argument)){
            configurator.setVerbose(true);
            result = true;
        }
        
        return result;
    }

    public String getKeyName() {
        return ARG_NAME;
    }
    
}
