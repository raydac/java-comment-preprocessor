package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;

public final class FunctionABS extends AbstractFunction {

    @Override
    public String getName() {
        return "abs";
    }

    public void execute(Expression stack, int index) {
       if (!stack.isThereOneValueBefore(index)) throw new IllegalStateException("Operation ABS needs an operand");

        Value _val0 = (Value)stack.getItemAtPosition(index-1);
        index--;
        stack.removeItemAt(index);

        switch (_val0.getType())
        {
            case INT:
                {
                    long l_result = Math.abs(((Long) _val0.getValue()).longValue());
                    stack.setItemAtPosition(index, new Value(Long.toString(l_result)));
                };break;
            case FLOAT:
                {
                    float f_result = Math.abs(((Float) _val0.getValue()).floatValue());
                    stack.setItemAtPosition(index, new Value(Float.toString(f_result)));
                };break;
            default :
                throw new IllegalArgumentException("Function ABS processes only the INTEGER or the FLOAT types");
        }
    }

    @Override
    public int getArity() {
        return 1;
    }

    
}
