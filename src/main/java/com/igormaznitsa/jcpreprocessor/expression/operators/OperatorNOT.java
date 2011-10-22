package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;

public class OperatorNOT extends AbstractOperator {

    @Override
    public boolean isUnary() {
        return true;
    }

    @Override
    public String getKeyword() {
        return "!";
    }

    public void execute(Expression stack, int index) {
        if (!stack.isThereOneValueBefore(index)) throw new IllegalStateException("Operation ! needs an operand");

        Value _val0 = (Value) stack.getItemAtPosition(index - 1);
        index--;
        stack.removeItemAt(index);

        switch (_val0.getType())
        {
            case BOOLEAN:
                {
                    boolean lg_result = !((Boolean) _val0.getValue()).booleanValue();
                    stack.setItemAtPosition(index, Value.valueOf(Boolean.valueOf(lg_result)));
                }
                ;
                break;
            case INT:
                {
                    long i_result = 0xFFFFFFFF ^ ((Long) _val0.getValue()).longValue();
                    stack.setItemAtPosition(index, Value.valueOf(Long.valueOf(i_result)));
                }
                ;
                break;
            default :
                throw new IllegalArgumentException("Operation ! processes only the BOOLEAN or the INTEGER types");
        }
    }

    public int getPriority() {
        return 4;
    }
    
}
