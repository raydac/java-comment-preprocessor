package com.igormaznitsa.jcpreprocessor.logger;

public interface PreprocessorLogger {
    void error(String text);
    void info(String text);
    void warning(String text);
}
