package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.File;

public final class FunctionROUND extends AbstractFunction {

    @Override
    public String getName() {
        return "round";
    }

    public void execute(PreprocessorContext context, Expression stack, int index) {
        if (!stack.isThereOneValueBefore(index)) throw new IllegalStateException("Operation ROUND needs an operand");

        final Value stackItem = (Value)stack.getItemAtPosition(index-1);
        index--;
        stack.removeItemAt(index);

        switch (stackItem.getType())
        {
            case INT:
                {
                    stack.setItemAtPosition(index, stackItem);
                };break;
            case FLOAT:
                {
                    final long result = Math.round(stackItem.asFloat().floatValue());
                    stack.setItemAtPosition(index, Value.valueOf(result));
                };break;
            default :
                throw new IllegalArgumentException("Function ROUND processes only the INTEGER or the FLOAT types");
        }

    }

    @Override
    public int getArity() {
        return 1;
    }
    
    
}
