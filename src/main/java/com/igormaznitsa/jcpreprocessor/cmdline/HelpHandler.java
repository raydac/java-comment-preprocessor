package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class HelpHandler implements CommandLineHandler {

    private static final String[] ARG_NAMES = new String[]{"/H", "/?", "-H", "-?"};

    public String getDescription() {
        return "print help about commands";
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
        boolean result = false;
        if (argument != null && !argument.isEmpty()) {
            
            final String argUpperCase = argument.trim().toUpperCase();
            
            for (final String str : ARG_NAMES) {
                if (str.equals(argUpperCase)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    public String getKeyName() {
        final StringBuilder result = new StringBuilder();
        for (int li = 0; li < ARG_NAMES.length; li++) {
            if (li > 0) {
                result.append(',');
            }
            result.append(ARG_NAMES[li]);
        }
        return result.toString();
    }
}
