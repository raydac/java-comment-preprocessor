package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.File;

public final class OperatorOR extends AbstractOperator {

    @Override
    public boolean isUnary() {
        return false;
    }

    @Override
    public String getKeyword() {
        return "||";
    }

    public void execute(PreprocessorContext context, Expression stack, int index) {
        if (!stack.areThereTwoValuesBefore(index)) {
            throw new IllegalArgumentException("Operation \'||\' needs two operands");
        }

        Value _val0 = (Value) stack.getItemAtPosition(index - 2);
        Value _val1 = (Value) stack.getItemAtPosition(index - 1);

        index -= 2;
        stack.removeItemAt(index);
        stack.removeItemAt(index);

        if (_val0.getType() != _val1.getType()) {
            throw new RuntimeException("Different value types detected");
        }

        switch (_val0.getType()) {
            case BOOLEAN: {
                Boolean result = ((Boolean) _val0.getValue()).booleanValue() || ((Boolean) _val1.getValue()).booleanValue();
                stack.setItemAtPosition(index, Value.valueOf(result));
            }

            break;
            case INT: {
                Long result = ((Long) _val0.getValue()).longValue() | ((Long) _val1.getValue()).longValue();
                stack.setItemAtPosition(index, Value.valueOf(Long.valueOf(result)));
            }
            break;
            default:
                throw new IllegalArgumentException("Operation || processes only the BOOLEAN or the INTEGER types");
        }

    }

    public int getPriority() {
        return 0;
    }
}
