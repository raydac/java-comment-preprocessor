package com.igormaznitsa.jcpreprocessor.cmd;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class ProcessorCharset implements CommandLineArgumentProcessor {

    private static final String ARG_NAME = "/T:";
    
    public String getKeyName() {
        return ARG_NAME;
    }

    public String getDescription() {
        return "set the charset for processing text files, default value is "+Configurator.DEFAULT_CHARSET;
    }

    public boolean processArgument(final String argument, final Configurator configurator) {
        if (argument.toUpperCase().startsWith(ARG_NAME)){
            configurator.setCharacterEncoding(PreprocessorUtils.extractTail(ARG_NAME, argument));
            return true;
        } else {
            return false;
        }
    }
    
}
