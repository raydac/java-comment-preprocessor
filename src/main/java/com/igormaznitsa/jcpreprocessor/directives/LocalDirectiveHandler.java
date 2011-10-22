package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.IOException;

public class LocalDirectiveHandler  extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "local";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public DirectiveBehaviour execute(final String string, final ParameterContainer state, final PreprocessorContext context) 
    throws IOException {
            processLocalDefinition(string, context);
            return DirectiveBehaviour.NORMAL;
    }

    private void processLocalDefinition(String _str, PreprocessorContext context) throws IOException
    {
            final String [] splitted = PreprocessorUtils.splitForChar(_str,'=');
        
            if (splitted.length!=2) {
                throw new IOException("Wrong expression ["+_str+']');
            }

            Value p_value = Expression.eval(splitted[1].trim(),context);

            if (p_value == null) throw new IOException("Error value");

            context.setLocalVariable(splitted[0].trim(), p_value);
    }

}
