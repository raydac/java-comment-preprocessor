package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class ExcludedFileExtensionsHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/EF:";
    
    public String getDescription() {
        return "set case insensetive file extensions to be excluded from preprocessing, they will not be processed and moved to the destination directory, default is ["+PreprocessorContext.DEFAULT_EXCLUDED_EXTENSIONS+']';
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
        if (argument.toUpperCase().startsWith(ARG_NAME)){
            configurator.setExcludedFileExtensions(PreprocessorUtils.extractTail(ARG_NAME, argument));
            return true;
        } else {
            return false;
        }
    }

    public String getKeyName() {
        return ARG_NAME;
    }
    
}
