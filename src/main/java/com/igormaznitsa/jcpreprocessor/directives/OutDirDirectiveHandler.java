package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.IOException;

public class OutDirDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "outdir";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext context) throws IOException {
        try {
            Value p_value = Expression.eval(string,context);

            if (p_value == null || p_value.getType() != ValueType.STRING) {
                throw new IOException("non string expression");
            }
            state.getFileReference().setDestinationDir((String) p_value.getValue());
        } catch (IOException e) {
            throw new IOException("You have the error in the #outdir instruction in the file " + state.getCurrentFileCanonicalPath() + " at line: " + state.getCurrentStringIndex() + " [" + e.getMessage() + ']');
        }
        return DirectiveBehaviour.NORMAL;
    }
}
