package com.igormaznitsa.jcpreprocessor.expression;

public final class Variable implements ExpressionStackItem {

    private final String name;
    
    public Variable(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ExpressionStackItemType getStackItemType() {
        return ExpressionStackItemType.VARIABLE;
    }

    public ExpressionStackItemPriority getPriority() {
        return ExpressionStackItemPriority.VALUE;
    }
    
}
