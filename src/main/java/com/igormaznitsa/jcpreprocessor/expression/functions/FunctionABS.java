package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

public final class FunctionABS extends AbstractFunction {

    private static final ValueType [][] ARGUMENT_TYPES = new ValueType[][]{{ValueType.INT},{ValueType.FLOAT}};
    
    @Override
    public String getName() {
        return "abs";
    }

    public Value executeInt(final PreprocessorContext context, final Value value) {
        return Value.valueOf(Long.valueOf((Math.abs(value.asLong().longValue()))));
    }
    
    public Value executeFloat(final PreprocessorContext context, final Value value) {
        return Value.valueOf(Float.valueOf((Math.abs(value.asFloat().floatValue()))));
    }
    
    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public ValueType[][] getAllowedArgumentTypes() {
        return ARGUMENT_TYPES;
    }

    @Override
    public String getReference() {
        return "it returns the absolute value of an argument";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.ANY;
    }


}
