package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class InCharsetHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/T:";

    public String getKeyName() {
        return ARG_NAME;
    }

    public String getDescription() {
        return "set the input charset for processing text files, default value is " + PreprocessorContext.DEFAULT_CHARSET;
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {

        boolean result = false;

        if (argument != null) {
            if (argument.toUpperCase().startsWith(ARG_NAME)) {
                final String value = PreprocessorUtils.extractTrimmedTail(ARG_NAME, argument);

                if (!value.isEmpty()) {
                    configurator.setInCharacterEncoding(value);
                    result = true;
                }
            }
        }
        return result;
    }
}
