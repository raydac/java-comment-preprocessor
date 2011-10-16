package com.igormaznitsa.jcpreprocessor.cmd;

import com.igormaznitsa.jcpreprocessor.cfg.Configurator;

public class ProcessorHelp implements CommandLineArgumentProcessor {

    private static final String [] ARG_NAMES = new String[]{"/H","/?","-H","-?"};
    
    public String getDescription() {
        return "print help about commands";
    }

    public boolean processArgument(final String argument, final Configurator configurator) {
        for(final String str : ARG_NAMES){
            if (str.equalsIgnoreCase(argument)){
                return true;
            }
        }
        return false;
    }

    public String getKeyName() {
        final StringBuilder result = new StringBuilder();
        for(int li=0;li<ARG_NAMES.length;li++){
            if (li>0){
                result.append(',');
            }
            result.append(ARG_NAMES[li]);
        }
        return result.toString();
    }
    
}
