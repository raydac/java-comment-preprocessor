package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class FileExtensionsHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/F:";
    
    public String getDescription() {
        return "set case sensetive file extensions which will be preprocessed, default is ["+PreprocessorContext.DEFAULT_PROCESSING_EXTENSIONS+']';
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
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
