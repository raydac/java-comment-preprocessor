package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.Value;

public final class OperatorAND extends AbstractOperator {

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public String getReference() {
        return "it makes AND operation over two operands";
    }


    @Override
    public String getKeyword() {
        return "&&";
    }

    public Value executeIntInt(final Value arg1, final Value arg2) {
        return Value.valueOf(Long.valueOf(arg1.asLong().longValue() & arg2.asLong().longValue()));
    }
    
    public Value executeBoolBool(final Value arg1, final Value arg2) {
        return Value.valueOf(Boolean.valueOf(arg1.asBoolean().booleanValue() && arg2.asBoolean().booleanValue()));
    }
    

     public ExpressionStackItemPriority getPriority() {
        return ExpressionStackItemPriority.LOGICAL;
    }
   
}
