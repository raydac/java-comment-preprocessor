package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.Value;

public final class OperatorADD extends AbstractOperator {

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public String getReference() {
        return "it allows to add two operands of compatible types";
    }

    @Override
    public String getKeyword() {
        return "+";
    }

    public Value executeIntInt(final Value arg1, final Value arg2) {
        return Value.valueOf(Long.valueOf(arg1.asLong().longValue()+arg2.asLong().longValue()));
    }
    
    public Value executeFloatFloat(final Value arg1, final Value arg2) {
        return Value.valueOf(Float.valueOf(arg1.asFloat().floatValue()+arg2.asFloat().floatValue()));
    }
    
    public Value executeIntFloat(final Value arg1, final Value arg2) {
        return Value.valueOf(Float.valueOf(arg1.asLong().floatValue()+arg2.asFloat().floatValue()));
    }
    
    public Value executeFloatInt(final Value arg1, final Value arg2) {
        return Value.valueOf(Float.valueOf(arg1.asFloat().floatValue()+arg2.asLong().floatValue()));
    }
    
    public Value executeStrAny(final Value arg1, final Value arg2) {
        return Value.valueOf(arg1.asString()+arg2.toString());
    }
    
    public Value executeAnyStr(final Value arg1, final Value arg2) {
        return Value.valueOf(arg1.toString()+arg2.asString());
    }
    

    public ExpressionStackItemPriority getPriority() {
        return ExpressionStackItemPriority.ARITHMETIC_ADD_SUB;
    }
    
}
