package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.File;

public final class OperatorSUB extends AbstractOperator {

    @Override
    public boolean isUnary() {
       return false;
    }

    @Override
    public String getKeyword() {
        return "-";
    }

    public void execute(File currentFile, Expression stack, int index) {
         Value _val0;
        Value _val1;

        if (!stack.areThereTwoValuesBefore(index))
        {
            if (stack.isThereOneValueBefore(index))
            {
                _val0 = new Value("0");
                _val1 = (Value) stack.getItemAtPosition(index - 1);
                index = index - 1;
                stack.removeElementAt(index);
            }
            else
            {
                throw new IllegalStateException("Operation \'-\' needs two operands");
            }
        }
        else
        {

            if (!stack.areThereTwoValuesBefore(index)) throw new IllegalStateException("Operation \'-\' needs two operands");

            _val0 = (Value) stack.getItemAtPosition(index - 2);
            _val1 = (Value) stack.getItemAtPosition(index - 1);

            index = index - 2;
            stack.removeElementAt(index);
            stack.removeElementAt(index);
        }

        if (_val0.getType() == ValueType.STRING || _val1.getType() == ValueType.STRING)
        {
            throw new IllegalArgumentException("You can't use \"-\" operation with the STRING type");
        }
        else
        {
            switch (_val0.getType())
            {
                case BOOLEAN:
                    {
                        throw new IllegalArgumentException("Operation \"-\" doesn't work with the BOOLEAN type and any other exclude STRING");
                    }

                case FLOAT:
                    {
                        float f_result;
                        if (_val0.getType() == _val1.getType())
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() - ((Float) _val1.getValue()).floatValue();
                        }
                        else
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() - ((Long) _val1.getValue()).intValue();
                        }
                        stack.setItemAtPosition(index, new Value(Float.toString(f_result)));
                    }
                    ;
                    break;
                case INT:
                    {
                        if (_val0.getType() == _val1.getType())
                        {
                            long i_result = ((Long) _val0.getValue()).longValue() - ((Long) _val1.getValue()).longValue();
                            stack.setItemAtPosition(index, new Value(Long.toString(i_result)));
                        }
                        else
                        {

                            float f_result = ((Long) _val0.getValue()).longValue() - ((Float) _val1.getValue()).floatValue();
                            stack.setItemAtPosition(index, new Value(Float.toString(f_result)));

                        }
                    }
                    ;
                    break;
            }
        }
    }

    public int getPriority() {
        return 2;
    }
    
}
