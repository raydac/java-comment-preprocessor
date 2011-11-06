package com.igormaznitsa.jcpreprocessor.logger;

public class SystemOutLogger implements PreprocessorLogger {

    public SystemOutLogger(){
        
    }
    
    public void error(final String text) {
        if (text != null) {
            final String out = "[ERROR]--> " + text;
            System.err.println(out);
        }
    }

    public void info(final String text) {
        if (text != null) {
            final String out = "[INFO]--> " + text;
            System.out.println(out);
        }
    }

    public void warning(final String text) {
        if (text != null) {
            final String out = "[WARNING]--> " + text;
            System.out.println(out);
        }
    }
}
