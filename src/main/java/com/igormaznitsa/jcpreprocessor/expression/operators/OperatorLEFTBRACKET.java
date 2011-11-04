package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;

public final class OperatorLEFTBRACKET extends AbstractOperator {

    @Override
    public boolean isUnary() {
        return true;
    }

    @Override
    public String getKeyword() {
        return "(";
    }

    public void execute(final PreprocessorContext context, final Expression stack, final int index) {
        throw new UnsupportedOperationException("Can't be executed");
    }

    public int getPriority() {
        return 7;
    }
    
}
