package com.igormaznitsa.jcpreprocessor.cmd;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;

public interface CommandLineHandler {
    String getKeyName();
    String getDescription();
    boolean processArgument(String argument, Configurator configurator);
}
