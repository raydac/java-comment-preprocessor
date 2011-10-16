package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;

public final class OperatorEQU extends AbstractOperator {

    @Override
    public boolean isUnary() {
        return false;
    }

    @Override
    public String getKeyword() {
        return "==";
    }

    public void execute(File currentFile, Expression stack, int index) {
        if (!stack.areThereTwoValuesBefore(index)) throw new IllegalStateException("Operation \'==\' needs two operands");

        Value _val0 = (Value) stack.getItemAtPosition(index - 2);
        Value _val1 = (Value) stack.getItemAtPosition(index - 1);

        index -= 2;
        stack.removeElementAt(index);
        stack.removeElementAt(index);


        if (_val0.getType() != _val1.getType()) throw new IllegalArgumentException("Incompatible types in \"==\" operation");

        switch (_val0.getType())
        {
            case BOOLEAN:
                {
                    boolean lg_result = ((Boolean) _val0.getValue()).booleanValue() == ((Boolean) _val1.getValue()).booleanValue();
                    stack.setItemAtPosition(index, new Value(Boolean.toString(lg_result)));
                }
                ;
                break;
            case FLOAT:
                {
                    boolean lg_result = ((Float) _val0.getValue()).floatValue() == ((Float) _val1.getValue()).floatValue();
                    stack.setItemAtPosition(index, new Value(Boolean.toString(lg_result)));
                }
                ;
                break;
            case INT:
                {
                    boolean lg_result = ((Long) _val0.getValue()).longValue() == ((Long) _val1.getValue()).longValue();
                    stack.setItemAtPosition(index, new Value(Boolean.toString(lg_result)));
                }
                ;
                break;
            case STRING:
                {
                    boolean lg_result = ((String) _val0.getValue()).equals((String) _val1.getValue());
                    stack.setItemAtPosition(index, new Value(Boolean.toString(lg_result)));
                }
                ;
                break;
            default: {
                throw new IllegalArgumentException("Unsupported argument");
            }
        }
    }

    public int getPriority() {
        return 1;
    }
    
}
