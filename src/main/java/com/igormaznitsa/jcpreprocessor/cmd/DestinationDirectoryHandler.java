package com.igormaznitsa.jcpreprocessor.cmd;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class DestinationDirectoryHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/O:";
    
    public String getDescription() {
        return "set the destination directory, default is "+Configurator.DEFAULT_DEST_DIRECTORY;
    }

    public boolean processArgument(final String argument, final Configurator configurator) {
        if (argument.toUpperCase().startsWith(ARG_NAME)){
            configurator.setDestinationDirectory(PreprocessorUtils.extractTail(ARG_NAME, argument));
            return true;
        } else {
            return false;
        }
    }

    public String getKeyName() {
        return ARG_NAME;
    }
    
}
