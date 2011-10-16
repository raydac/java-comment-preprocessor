package com.igormaznitsa.jcpreprocessor.cmd;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class ProcessorSourceDirectory implements CommandLineArgumentProcessor {

    private static final String ARG_NAME = "/I:";
    
    public String getDescription() {
        return "set the source root directory to be preprocessed, default is "+Configurator.DEFAULT_SOURCE_DIRECTORY;
    }

    public boolean processArgument(final String argument, final Configurator configurator) {
        if (argument.toUpperCase().startsWith(ARG_NAME)){
            configurator.setSourceDirectory(PreprocessorUtils.extractTail(ARG_NAME, argument));
            return true;
        } else {
            return false;
        }
    }

    public String getKeyName() {
        return ARG_NAME;
    }
    
}
