package com.igormaznitsa.jcpreprocessor.expression;

public interface ExpressionStackItem {
    ExpressionStackItemType getStackItemType();
    int getPriority();
}
