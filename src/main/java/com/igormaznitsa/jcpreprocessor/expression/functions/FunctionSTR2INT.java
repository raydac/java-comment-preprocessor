package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;

public final class FunctionSTR2INT extends AbstractFunction {

    @Override
    public String getName() {
        return "str2int";
    }

    public void execute(Expression stack, int index) {
        if (!stack.isThereOneValueBefore(index)) throw new IllegalStateException("Operation STR2INT needs an operand");

        Value _val0 = (Value)stack.getItemAtPosition(index-1);
        index--;
        stack.removeItemAt(index);

        switch (_val0.getType())
        {
            case STRING:
                {
                    String s_result = (String) _val0.getValue();

                    long l_value = 0;

                    try
                    {
                        l_value = Long.parseLong(s_result);
                    } catch (NumberFormatException e)
                    {
                        throw new IllegalArgumentException("I can't convert value ["+s_result+']');
                    }
                   stack.setItemAtPosition(index, new Value(new Long(l_value)));
                };break;
            default :
                throw new IllegalArgumentException("Function STR2INT processes only the STRING type");
        }

    }
    
    @Override
    public int getArity() {
        return 1;
    }
    
}
