package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.IOException;

public class ExitIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "exitif";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext context) throws IOException {
        // To end processing the file processing immediatly if the value is true
        final Value condition = Expression.eval(string,context);
        if (condition == null || condition.getType() != ValueType.BOOLEAN) {
            throw new IOException("You must use a boolean argument for an #endif operator");
        }
        if (((Boolean) condition.getValue()).booleanValue()) {
            state.setEndPreprocessing(true);
            return DirectiveBehaviour.CONTINUE;
        }
        return DirectiveBehaviour.NORMAL;
    }

 }
