package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public class RemoveCommentsHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/R";
    
    public String getDescription() {
        return "after preprocessing the new file will be completely cleared of comments in Java-C style";
    }

    public boolean processArgument(final String argument, final PreprocessorContext configurator) {
        boolean result = false;
        
        if (ARG_NAME.equalsIgnoreCase(argument)){
            configurator.setRemovingComments(true);
            result = true;
        }
        
        return result;
    }

    public String getKeyName() {
        return ARG_NAME;
    }
    
}
