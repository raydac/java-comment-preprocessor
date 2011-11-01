package com.igormaznitsa.jcpreprocessor.cmdline;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

public class GlobalVariableHandler implements CommandLineHandler {

    private static final String ARG_NAME = "/P:";
    
    public String getDescription() {
        return "set a global variable, for instance /P:DEBUG=true";
    }

    public boolean processArgument(final String argument, final PreprocessorContext context) {
        if (argument.toUpperCase().startsWith(ARG_NAME)){
            
            final String nameAndExpression = PreprocessorUtils.extractTrimmedTail(ARG_NAME, argument);
            
            final String [] splitted = PreprocessorUtils.splitForSetOperator(argument);
            if (splitted.length!=2){
                throw new RuntimeException("Wrong expression at a "+ARG_NAME+" directive ["+nameAndExpression+']');
            }
            
            final String value = splitted[0];
            final String expression = splitted[1];
            
            if (context.containsGlobalVariable(value)){
                throw new IllegalArgumentException("Duplicated global definition detected ["+value+']');
            }
            
            final Value result = Expression.eval(expression, context, null);
            context.setGlobalVariable(value, result, null);
            return true;
        } else {
            return false;
        }
    }

    public String getKeyName() {
        return ARG_NAME;
    }
    
}
