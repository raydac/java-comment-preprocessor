package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;

public final class OperatorLESS extends AbstractOperator {

    @Override
    public boolean isUnary() {
        return false;
    }

    @Override
    public String getKeyword() {
        return "<";
    }

    public void execute(PreprocessorContext context, Expression stack, int index) {
        if (!stack.areThereTwoValuesBefore(index)) throw new IllegalStateException("Operation \'<\' needs two operands");

        Value _val0 = (Value) stack.getItemAtPosition(index - 2);
        Value _val1 = (Value) stack.getItemAtPosition(index - 1);

        index -= 2;
        stack.removeItemAt(index);
        stack.removeItemAt(index);

        if (_val0.getType() != _val1.getType()) throw new IllegalArgumentException("Incompatible types in \"<\" operation");

        Boolean result;
        
        switch (_val0.getType())
        {
            case BOOLEAN:
                {
                    throw new IllegalArgumentException("Operation \"<\" doesn't work with BOOLEAN types");
                }
            case FLOAT:
                {
                    result = ((Float) _val0.getValue()).floatValue() < ((Float) _val1.getValue()).floatValue();
                }
                break;
            case INT:
                {
                    result = ((Long) _val0.getValue()).longValue() < ((Long) _val1.getValue()).longValue();
                }
                break;
            case STRING:
                {
                    result = ((String) _val0.getValue()).length() < ((String) _val1.getValue()).length();
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported type");
        }
        stack.setItemAtPosition(index, Value.valueOf(result));
    }

    public int getPriority() {
        return 1;
    }
    
}
