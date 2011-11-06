package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

public final class FunctionSTR2INT extends AbstractFunction {

    private static final ValueType [][] ARG_TYPES = new ValueType[][]{{ValueType.STRING}};
    
    @Override
    public String getName() {
        return "str2int";
    }

    public Value executeStr(final PreprocessorContext context, final Value value) {
        return Value.valueOf(Long.valueOf(Long.parseLong(value.asString())));
    }
    
    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public ValueType[][] getAllowedArgumentTypes() {
        return ARG_TYPES;
    }

    @Override
    public String getReference() {
        return "it converts a string into an integer";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.INT;
    }
    
    
    
}
