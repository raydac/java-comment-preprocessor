package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;

public final class FunctionSTRLEN extends AbstractFunction {

    @Override
    public String getName() {
        return "strlen";
    }

    public void execute(Expression stack, int index) {
        if (!stack.isThereOneValueBefore(index)) {
            throw new IllegalStateException("Operation STRLEN needs an operand");
        }

        Value _val0 = (Value) stack.getItemAtPosition(index - 1);
        index--;
        stack.removeItemAt(index);

        switch (_val0.getType()) {
            case STRING: {
                String s_result = (String) _val0.getValue();
                final long l_len = s_result.length();
                stack.setItemAtPosition(index, Value.valueOf(Long.valueOf(l_len)));
            }
            ;
            break;
            default:
                throw new IllegalArgumentException("Function STRLEN processes only the STRING types");
        }

    }
    @Override
    public int getArity() {
        return 1;
    }
}    

