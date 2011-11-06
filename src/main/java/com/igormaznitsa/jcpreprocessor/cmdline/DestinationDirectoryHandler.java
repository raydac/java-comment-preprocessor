package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class DestinationDirectoryHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/O:";

    public String getDescription() {
        return "set the destination directory, default is " + PreprocessorContext.DEFAULT_DEST_DIRECTORY;
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
        boolean result = false;

        if (argument != null && !argument.isEmpty()) {

            if (argument.toUpperCase().startsWith(ARG_NAME)) {
                final String name = PreprocessorUtils.extractTrimmedTail(ARG_NAME, argument);
                if (!name.isEmpty()) {
                    configurator.setDestinationDirectory(PreprocessorUtils.extractTail(ARG_NAME, argument));
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
