package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class CharsetHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/T:";
    
    public String getKeyName() {
        return ARG_NAME;
    }

    public String getDescription() {
        return "set the charset for processing text files, default value is "+PreprocessorContext.DEFAULT_CHARSET;
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
        if (argument.toUpperCase().startsWith(ARG_NAME)){
            configurator.setCharacterEncoding(PreprocessorUtils.extractTail(ARG_NAME, argument));
            return true;
        } else {
            return false;
        }
    }
    
}
