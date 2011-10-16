package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.AbstractExpressionExecutor;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItem;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemType;

public abstract class NewOperator implements AbstractExpressionExecutor, ExpressionStackItem {

    public ExpressionStackItemType getStackItemType() {
        return ExpressionStackItemType.OPERATOR;
    }
    
    public abstract boolean isUnary();
}
