package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import java.io.File;

public final class OperatorLEFTBRACKET extends AbstractOperator {

    @Override
    public boolean isUnary() {
        return true;
    }

    @Override
    public String getKeyword() {
        return "(";
    }

    public void execute(File currentFile, Expression stack, int index) {
        throw new UnsupportedOperationException("Can't be executed");
    }

    public int getPriority() {
        return 7;
    }
    
}
