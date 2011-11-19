package com.igormaznitsa.jcpreprocessor.expression;

public interface ExpressionStackItem {
    ExpressionStackItemType getStackItemType();
    ExpressionStackItemPriority getPriority();
}
