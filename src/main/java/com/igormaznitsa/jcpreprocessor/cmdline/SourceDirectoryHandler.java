package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class SourceDirectoryHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/I:";

    public String getDescription() {
        return "set the source root directory to be preprocessed, default is " + PreprocessorContext.DEFAULT_SOURCE_DIRECTORY;
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
        boolean result = false;

        if (argument != null && !argument.isEmpty()) {
            if (argument.toUpperCase().startsWith(ARG_NAME)) {
                final String tail = PreprocessorUtils.extractTrimmedTail(ARG_NAME, argument);
                if (!tail.isEmpty()) {
                    configurator.setSourceDirectory(PreprocessorUtils.extractTail(ARG_NAME, argument));
                    result = true;
                }
            }
        }

        return result;
    }

    public String getKeyName() {
        return ARG_NAME;
    }
}
