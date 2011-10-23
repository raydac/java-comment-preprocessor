package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class SourceDirectoryHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/I:";
    
    public String getDescription() {
        return "set the source root directory to be preprocessed, default is "+PreprocessorContext.DEFAULT_SOURCE_DIRECTORY;
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
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
