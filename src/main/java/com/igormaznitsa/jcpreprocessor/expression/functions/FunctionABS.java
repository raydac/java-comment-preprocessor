package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;

public final class FunctionABS extends AbstractFunction {

    @Override
    public String getName() {
        return "abs";
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
            ;
            break;
            case FLOAT: {
                stack.setItemAtPosition(index, Value.valueOf(Math.abs(value.asFloat())));
            }
            ;
            break;
            default:
                throw new IllegalArgumentException("Function ABS processes only the INTEGER or the FLOAT types");
        }
    }

    @Override
    public int getArity() {
        return 1;
    }
}
