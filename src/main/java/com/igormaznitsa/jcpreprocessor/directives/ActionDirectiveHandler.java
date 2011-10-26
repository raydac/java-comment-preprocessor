package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItem;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemType;
import com.igormaznitsa.jcpreprocessor.expression.Value;

public class ActionDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "action";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getReference() {
        return null;
    }

    @Override
    public DirectiveBehaviour execute(final String string, final ParameterContainer state, final PreprocessorContext context) {
        if (context.getPreprocessorExtension() != null) {
            final String stringToBeProcessed = string.trim();
            Expression p_stack = Expression.prepare(stringToBeProcessed, context);
            p_stack.eval();

            Value[] ap_results = new Value[p_stack.size()];
            for (int li = 0; li < p_stack.size(); li++) {
                ExpressionStackItem p_obj = p_stack.getItemAtPosition(li);
                if (p_obj.getStackItemType() != ExpressionStackItemType.VALUE) {
                    throw new RuntimeException("Wrong argument detected");
                }
                ap_results[li] = (Value) p_obj;
            }

            if (!context.getPreprocessorExtension().processUserDirective(ap_results, state)) {
                throw new RuntimeException("Extension can't process the action");
            }
        }
        return DirectiveBehaviour.PROCESSED;
    }
}
