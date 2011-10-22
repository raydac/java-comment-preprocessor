package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;

public final class OperatorNOTEQU extends AbstractOperator {

    @Override
    public boolean isUnary() {
        return false;
    }

    @Override
    public String getKeyword() {
        return "!=";
    }

    public void execute(Expression stack, int index) {
         if (!stack.areThereTwoValuesBefore(index)) throw new IllegalStateException("Operation \'!=\' needs two operands");

        Value _val0 = (Value) stack.getItemAtPosition(index - 2);
        Value _val1 = (Value) stack.getItemAtPosition(index - 1);

        index -= 2;
        stack.removeItemAt(index);
        stack.removeItemAt(index);

        if (_val0.getType() != _val1.getType()) throw new IllegalArgumentException("Incompatible types in \"!=\" operation");

        Boolean result;
        
        switch (_val0.getType())
        {
            case BOOLEAN:
                {
                    result = ((Boolean) _val0.getValue()).booleanValue() != ((Boolean) _val1.getValue()).booleanValue();
                }
                break;
            case FLOAT:
                {
                    result = ((Float) _val0.getValue()).floatValue() != ((Float) _val1.getValue()).floatValue();
                }
                
                break;
            case INT:
                {
                    result = ((Long) _val0.getValue()).longValue() != ((Long) _val1.getValue()).longValue();
                }
                
                break;
            case STRING:
                {
                    result = !(((String) _val0.getValue()).equals((String) _val1.getValue()));
                }
                
                break;
            default: throw new RuntimeException("Unsupported type");
        }
       stack.setItemAtPosition(index, Value.valueOf(result));

    }

    public int getPriority() {
        return 1;
    }
    
}
