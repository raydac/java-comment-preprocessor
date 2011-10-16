package com.igormaznitsa.jcpreprocessor.cmd;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class ProcessorProcessingFileExtensions implements CommandLineArgumentProcessor {

    private static final String ARG_NAME = "/F:";
    
    public String getDescription() {
        return "set case sensetive file extensions which will be preprocessed, default is ["+Configurator.DEFAULT_PROCESSING_EXTENSIONS+']';
    }

    public boolean processArgument(final String argument, final Configurator configurator) {
        if (argument.toUpperCase().startsWith(ARG_NAME)){
            
            configurator.setProcessingFileExtensions(PreprocessorUtils.extractTail(ARG_NAME, argument));
            
            return true;
        } else {
            return false;
        }
    }

    public String getKeyName() {
        return ARG_NAME;
    }
    
}
