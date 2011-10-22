package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItem;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemType;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.IOException;

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
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext context) throws IOException {
        if (state.isOutEnabled()) {
            // Вызов внешнего обработчика, если есть
            if (context.getPreprocessorExtension() != null) {
                String stringToBeProcessed = string.trim();
                Expression p_stack = Expression.prepare(stringToBeProcessed,context);
                p_stack.eval();

                Value[] ap_results = new Value[p_stack.size()];
                for (int li = 0; li < p_stack.size(); li++) {
                    ExpressionStackItem p_obj = p_stack.getItemAtPosition(li);
                    if (p_obj.getStackItemType() != ExpressionStackItemType.VALUE) {
                        throw new IOException("Error arguments list \'" + stringToBeProcessed + "\'");
                    }
                    ap_results[li] = (Value) p_obj;
                }

                if (!context.getPreprocessorExtension().processAction(ap_results, state.getFileReference().getDestinationDir(), state.getFileReference().getDestinationName(), state.getNormalOutStream(), state.getPrefixOutStream(), state.getPostfixOutStream(), context.getInfoPrintStream())) {
                    throw new IOException("There is an error during an action processing [" + stringToBeProcessed + "]");
                }
            }
        }
        return DirectiveBehaviour.NORMAL;
    }
}
