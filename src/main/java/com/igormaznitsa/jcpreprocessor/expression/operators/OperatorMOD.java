package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.File;

public final class OperatorMOD extends AbstractOperator {

    @Override
    public boolean isUnary() {
        return false;
    }

    @Override
    public String getKeyword() {
        return "%";
    }

    public void execute(File currentFile, Expression stack, int index) {
        if (!stack.areThereTwoValuesBefore(index)) throw new IllegalStateException("Operation \'%\' needs two operands");

        Value _val0 = (Value) stack.getItemAtPosition(index - 2);
        Value _val1 = (Value) stack.getItemAtPosition(index - 1);

        index = index - 2;
        stack.removeElementAt(index);
        stack.removeElementAt(index);

        if (_val0 == null || _val1 == null) throw new IllegalStateException("Operation \'%\' needs two operands");
        if (_val0.getType() == ValueType.STRING || _val1.getType() == ValueType.STRING)
        {
            throw new IllegalArgumentException("You can't use \"%\" operation with the STRING type");
        }
        else
        {
            switch (_val0.getType())
            {
                case BOOLEAN:
                    {
                        throw new IllegalArgumentException("Operation \"%\" doesn't work with the BOOLEAN type and any other exclude STRING");
                    }

                case FLOAT:
                    {
                        float f_result;
                        if (_val0.getType() == _val1.getType())
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() % ((Float) _val1.getValue()).floatValue();
                        }
                        else
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() % ((Long) _val1.getValue()).longValue();
                        }
                        stack.setItemAtPosition(index, new Value(Float.toString(f_result)));
                    }
                    ;
                    break;

                case INT:
                    {
                        if (_val0.getType() == _val1.getType())
                        {
                            long i_result = ((Long) _val0.getValue()).longValue() % ((Long) _val1.getValue()).longValue();
                            stack.setItemAtPosition(index, new Value(Long.toString(i_result)));
                        }
                        else
                        {
                            float f_result = ((Long) _val0.getValue()).longValue() % ((Float) _val1.getValue()).floatValue();
                            stack.setItemAtPosition(index, new Value(Float.toString(f_result)));
                        }
                    }
                    ;
                    break;
            }
        }
   }

    public int getPriority() {
        return 3;
    }
    
}
