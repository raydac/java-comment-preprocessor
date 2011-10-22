package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.File;

public class OperatorXOR extends AbstractOperator {

    @Override
    public boolean isUnary() {
        return false;
    }

    @Override
    public String getKeyword() {
        return "^";
    }

    public void execute(PreprocessorContext context, Expression stack, int index) {
        if (!stack.areThereTwoValuesBefore(index)) throw new IllegalStateException("Operation \'^\' needs two operands");

        Value _val0 = (Value) stack.getItemAtPosition(index - 2);
        Value _val1 = (Value) stack.getItemAtPosition(index - 1);

        index -= 2;
        stack.removeItemAt(index);
        stack.removeItemAt(index);

        switch (_val0.getType())
        {
            case BOOLEAN:
                {
                    if (_val1.getType() == ValueType.BOOLEAN)
                    {
                        boolean lg_result = ((Boolean) _val0.getValue()).booleanValue() ^ ((Boolean) _val1.getValue()).booleanValue();
                        stack.setItemAtPosition(index, Value.valueOf(Boolean.valueOf(lg_result)));
                    }
                }
                ;
                break;
            case INT:
                {
                    if (_val1.getType() == ValueType.INT)
                    {
                        long i_result = ((Long) _val0.getValue()).longValue() ^ ((Long) _val1.getValue()).longValue();
                        stack.setItemAtPosition(index, Value.valueOf(Long.valueOf(i_result)));
                    }
                }
                ;
                break;
            default :
                throw new IllegalArgumentException("Operation \'^\' processes only the BOOLEAN or the INTEGER types");
        }

    }

    public int getPriority() {
       return 0;
    }
    
}
