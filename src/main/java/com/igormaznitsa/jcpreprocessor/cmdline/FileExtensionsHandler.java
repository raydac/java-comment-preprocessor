package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class FileExtensionsHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/F:";

    public String getDescription() {
        return "set case sensetive file extensions which will be preprocessed, default is [" + PreprocessorContext.DEFAULT_PROCESSING_EXTENSIONS + ']';
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
        boolean result = false;

        if (argument != null && !argument.isEmpty()) {

            if (argument.toUpperCase().startsWith(ARG_NAME)) {
                final String extensions = PreprocessorUtils.extractTrimmedTail(ARG_NAME, argument);
                
                if (!extensions.isEmpty()) {
                    configurator.setProcessingFileExtensions(extensions);
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
