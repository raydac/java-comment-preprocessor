package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.Value;

public final class OperatorLESSEQU extends AbstractOperator {

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public String getReference() {
        return "fake description";
    }

    @Override
    public String getKeyword() {
        return "<=";
    }

    public Value executeIntInt(final Value arg1, final Value arg2) {
        return Value.valueOf(Boolean.valueOf(arg1.asLong() <= arg2.asLong()));
    }
    
    public Value executeFloatInt(final Value arg1, final Value arg2) {
        return Value.valueOf(Boolean.valueOf(Float.compare(arg1.asFloat().floatValue(), arg2.asLong().floatValue()) <= 0));
    }
    
    public Value executeIntFloat(final Value arg1, final Value arg2) {
        return Value.valueOf(Boolean.valueOf(Float.compare(arg1.asLong().floatValue(), arg2.asFloat().floatValue()) <= 0));
    }
    
    public Value executeFloatFloat(final Value arg1, final Value arg2) {
        return Value.valueOf(Boolean.valueOf(Float.compare(arg1.asFloat().floatValue(), arg2.asFloat().floatValue()) <= 0));
    }
    
    public Value executeStrStr(final Value arg1, final Value arg2) {
        return Value.valueOf(Boolean.valueOf(arg1.asString().compareTo(arg2.asString()) <= 0));
    }
    
    public ExpressionStackItemPriority getPriority() {
        return ExpressionStackItemPriority.COMPARISON;
    }
}
