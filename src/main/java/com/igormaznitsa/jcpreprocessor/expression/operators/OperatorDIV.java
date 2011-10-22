package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.File;

public final class OperatorDIV extends AbstractOperator {

    @Override
    public boolean isUnary() {
        return false;
    }

    @Override
    public String getKeyword() {
        return "/";
    }

    public void execute(PreprocessorContext context, Expression _stack, int _index) {
       if (!_stack.areThereTwoValuesBefore(_index)) throw new IllegalStateException("Operation \'\\\' needs two operands");

        Value _val0 = (Value) _stack.getItemAtPosition(_index - 2);
        Value _val1 = (Value) _stack.getItemAtPosition(_index - 1);

        _index = _index - 2;
        _stack.removeItemAt(_index);
        _stack.removeItemAt(_index);

        if (_val0.getType() == ValueType.STRING || _val1.getType() == ValueType.STRING)
        {
            throw new IllegalArgumentException("You can't use \"/\" operation with the STRING type");
        }
        else
        {
            switch (_val0.getType())
            {
                case BOOLEAN:
                    {
                        throw new IllegalArgumentException("Operation \"/\" doesn't work with the BOOLEAN type and any other exclude STRING");
                    }

                case FLOAT:
                    {
                        float f_result;
                        if (_val0.getType() == _val1.getType())
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() / ((Float) _val1.getValue()).floatValue();
                        }
                        else
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() / ((Long) _val1.getValue()).longValue();
                        }
                        _stack.setItemAtPosition(_index, Value.valueOf(Float.valueOf(f_result)));
                    }
                    ;
                    break;

                case INT:
                    {
                        if (_val0.getType() == _val1.getType())
                        {
                            long i_result = ((Long) _val0.getValue()).longValue() / ((Long) _val1.getValue()).longValue();
                            _stack.setItemAtPosition(_index, Value.valueOf(Long.valueOf(i_result)));
                        }
                        else
                        {

                            float f_result = ((Long) _val0.getValue()).longValue() / ((Float) _val1.getValue()).floatValue();
                            _stack.setItemAtPosition(_index, Value.valueOf(Float.valueOf(f_result)));
                        }
                    }
                    ;
                    break;
                default: throw new IllegalArgumentException("Unknown type");
            }
        }
    }

    public int getPriority() {
        return 3;
    }
    
}
