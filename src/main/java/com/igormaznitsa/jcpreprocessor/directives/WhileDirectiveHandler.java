package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.IOException;

public class WhileDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "while";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext context) throws IOException {
        if (state.isProcessingEnabled()) {
            String stringToBeProcessed = string.trim();
            Value p_value = Expression.eval(stringToBeProcessed,context);
            if (p_value == null || p_value.getType() != ValueType.BOOLEAN) {
                throw new IOException("You don't have a boolean result in the #while instruction");
            }
            if (state.isWhileCounterZero()) {
                state.setLastWhileFileName(state.getCurrentFileCanonicalPath());
                state.setLastWhileStringNumber(state.getCurrentStringIndex());
            }
            state.increaseWhileCounter();
            state.setActiveWhileCounter(state.getWhileCounter());

            if (((Boolean) p_value.getValue()).booleanValue()) {
                state.setThereIsNoBreakCommand(true);
            } else {
                state.setThereIsNoBreakCommand(false);
            }
        } else {
            state.increaseWhileCounter();
        }
        state.pushWhileIndex(state.getCurrentStringIndex() - 1);
        
        return DirectiveBehaviour.NORMAL;
    }

    @Override
    public boolean processOnlyIfProcessingEnabled() {
        return false;
    }
}
