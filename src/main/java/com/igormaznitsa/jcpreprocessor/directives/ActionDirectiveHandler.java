package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
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
        return "it calls an outside processor and give arguments to it";
    }

    @Override
    public String getExpressionType() {
        return "EXPR1,EXPR2...EXPRn";
    }

    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context) {
        if (context.getPreprocessorExtension() != null) {

            final Expression stack = Expression.prepare(string.trim(), context, state);
            
            final Expression [] args = stack.splitForDelimiters();
            
            final Value[] results = new Value[args.length];
            int index = 0;
            for (final Expression expr : args) {
                final Value val = expr.eval();
                if (val==null) {
                    throw new RuntimeException("Wrong expression at argument "+(index+1));
                }
                results[index++] = val;
            }

            if (!context.getPreprocessorExtension().processAction(results, state)) {
                throw new RuntimeException("Extension can't process the action");
            }
        }
        return AfterProcessingBehaviour.PROCESSED;
    }
}
