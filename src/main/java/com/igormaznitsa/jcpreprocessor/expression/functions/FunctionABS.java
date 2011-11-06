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
    
    public void execute(final PreprocessorContext context, final Expression stack, final int stackPos) {
        if (!stack.isThereOneValueBefore(stackPos)) {
            throw new IllegalStateException("Operation ABS needs an operand");
        }

        final int index = stackPos - 1;
        final Value value = (Value) stack.getItemAtPosition(index);
        stack.removeItemAt(index);

        switch (value.getType()) {
            case INT: {
                stack.setItemAtPosition(index, Value.valueOf(Math.abs(value.asLong())));
            }
            break;
            case FLOAT: {
                stack.setItemAtPosition(index, Value.valueOf(Math.abs(value.asFloat())));
            }
            break;
            default:
                throw new IllegalArgumentException("Function ABS processes only the INTEGER or the FLOAT types");
        }
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
