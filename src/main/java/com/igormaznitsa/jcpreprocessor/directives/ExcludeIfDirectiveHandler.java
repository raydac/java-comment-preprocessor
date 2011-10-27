package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.IOException;

public class ExcludeIfDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "excludeif";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getReference() {
        return null;
    }

    @Override
    public boolean isFirstPassAllowed() {
        return true;
    }

    @Override
    public boolean isSecondPassAllowed() {
        return false;
    }

    @Override
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext context) {
        state.pushExcludeIfData(state.getRootFileInfo(), string, state.peekFile().getNextStringIndex()-1);
        return DirectiveBehaviour.PROCESSED;
    }
}
