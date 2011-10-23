package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
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
    public String getReference() {
        return null;
    }

    @Override
    public DirectiveBehaviourEnum execute(String string, ParameterContainer state, PreprocessorContext context) {
        if (state.isProcessingEnabled()) {
            String stringToBeProcessed = string.trim();
            Value p_value = Expression.eval(stringToBeProcessed,context);
            if (p_value == null || p_value.getType() != ValueType.BOOLEAN) {
                throw new RuntimeException("//#while needs a boolean expression");
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
        
        return DirectiveBehaviourEnum.PROCESSED;
    }

    @Override
    public boolean processOnlyIfProcessingEnabled() {
        return false;
    }
}
