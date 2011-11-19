package com.igormaznitsa.jcpreprocessor.expression;

public enum ExpressionStackItemPriority {
    LOGICAL(0),
    COMPARISON(1),
    ARITHMETIC_ADD_SUB(2),
    ARITHMETIC_MUL_DIV_MOD(3),
    FUNCTION(5),
    VALUE(6);
    
    private final int priority;
    
    public int getPriority() {
        return priority;
    }
    
    private ExpressionStackItemPriority(final int priority) {
        this.priority = priority;
    }
}
