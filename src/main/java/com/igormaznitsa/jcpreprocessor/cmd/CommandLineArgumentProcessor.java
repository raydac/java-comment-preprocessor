package com.igormaznitsa.jcpreprocessor.cmd;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;

public interface CommandLineArgumentProcessor {
    String getKeyName();
    String getDescription();
    boolean processArgument(String argument, Configurator configurator);
}
