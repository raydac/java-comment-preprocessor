package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.Value;

public class OperatorNOT extends AbstractOperator {

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public String getReference() {
        return "fake reference";
    }

    @Override
    public String getKeyword() {
        return "!";
    }

   public Value executeInt(final Value arg1) {
        return Value.valueOf(Long.valueOf(0xFFFFFFFFFFFFFFFFL^arg1.asLong().longValue()));
    }
    
    public Value executeBool(final Value arg1) {
        return Value.valueOf(Boolean.valueOf(!arg1.asBoolean().booleanValue()));
    }

     public ExpressionStackItemPriority getPriority() {
        return ExpressionStackItemPriority.FUNCTION;
    }
}
