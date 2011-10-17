package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.File;

public final class OperatorADD extends AbstractOperator {

    @Override
    public boolean isUnary() {
        return false;
    }

    @Override
    public String getKeyword() {
        return "+";
    }

    public void execute(Expression stack, int index) {
        if (!stack.areThereTwoValuesBefore(index)) throw new IllegalStateException("Operation \'+\' needs two operands");

        Value _val0 = (Value) stack.getItemAtPosition(index - 2);
        Value _val1 = (Value) stack.getItemAtPosition(index - 1);

        index -= 2;
        stack.removeItemAt(index);
        stack.removeItemAt(index);

        if (_val0.getType() == ValueType.STRING || _val1.getType() == ValueType.STRING)
        {
            String s_val0 = "", s_val1 = "";
            if (_val0.getType() != ValueType.STRING)
            {
                switch (_val0.getType())
                {
                    case BOOLEAN:
                        {
                            s_val0 = "" + ((Boolean) _val0.getValue()).booleanValue();
                        }
                        ;
                        break;
                    case INT:
                        {
                            s_val0 = "" + ((Long) _val0.getValue()).longValue();
                        }
                        ;
                        break;
                    case FLOAT:
                        {
                            s_val0 = "" + ((Float) _val0.getValue()).floatValue();
                        }
                        ;
                        break;
                }
            }
            else
            {
                s_val0 = (String) _val0.getValue();
            }

            if (_val1.getType() != ValueType.STRING)
            {
                switch (_val1.getType())
                {
                    case BOOLEAN:
                        {
                            s_val1 = "" + ((Boolean) _val1.getValue()).booleanValue();
                        }
                        ;
                        break;
                    case INT:
                        {
                            s_val1 = "" + ((Long) _val1.getValue()).longValue();
                        }
                        ;
                        break;
                    case FLOAT:
                        {
                            s_val1 = "" + ((Float) _val1.getValue()).floatValue();
                        }
                        ;
                        break;
                }
            }
            else
            {
                s_val1 = (String) _val1.getValue();
            }

            s_val0 = s_val0.concat(s_val1);
            stack.setItemAtPosition(index, new Value("\"" + s_val0 + "\""));
        }
        else
        {
            switch (_val0.getType())
            {
                case BOOLEAN:
                    {
                        throw new IllegalArgumentException("Operation \"+\" doesn't work with the BOOLEAN type and any other exclude STRING");
                    }

                case FLOAT:
                    {
                        float f_result;
                        if (_val0.getType() == _val1.getType())
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() + ((Float) _val1.getValue()).floatValue();
                        }
                        else
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() + ((Long) _val1.getValue()).longValue();
                        }
                        stack.setItemAtPosition(index, new Value(Float.toString(f_result)));
                    }
                    ;
                    break;
                case INT:
                    {
                        if (_val0.getType() == _val1.getType())
                        {
                            long i_result = ((Long) _val0.getValue()).longValue() + ((Long) _val1.getValue()).longValue();
                            stack.setItemAtPosition(index, new Value(Long.toString(i_result)));
                        }
                        else
                        {
                            float f_result = ((Long) _val0.getValue()).longValue() + ((Float) _val1.getValue()).floatValue();
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
