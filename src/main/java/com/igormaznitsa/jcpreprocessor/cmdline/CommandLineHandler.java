package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public interface CommandLineHandler {
    String getKeyName();
    String getDescription();
    boolean processArgument(String argument, PreprocessorContext configurator);
}
