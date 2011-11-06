package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

public final class FunctionROUND extends AbstractFunction {

    private static final ValueType [][] SIGNATURES = new ValueType[][]{{ValueType.FLOAT},{ValueType.INT}};
    
    @Override
    public String getName() {
        return "round";
    }

    public Value executeInt(final PreprocessorContext context, final Value value) {
        return value;
    }
    
    public Value executeFloat(final PreprocessorContext context, final Value value) {
        return Value.valueOf(Long.valueOf(Math.round(value.asFloat())));
    }
    
    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public ValueType[][] getAllowedArgumentTypes() {
        return SIGNATURES;
    }

    @Override
    public String getReference() {
        return "it returns closest integer value to the argument";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.INT;
    }
    
    
}
