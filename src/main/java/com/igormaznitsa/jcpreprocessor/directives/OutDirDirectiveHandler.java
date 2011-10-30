package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.JCPSpecialVariables;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

public class OutDirDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "outdir";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public String getReference() {
        return "it allows to change the output directory name for the preprocessing file (it can be read through "+JCPSpecialVariables.VAR_DEST_DIR+')';
    }

    @Override
    public String getExpressionType() {
        return "STRING";
    }
    
    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context) {
        final Value name = Expression.eval(string, context, state);

        if (name == null || name.getType() != ValueType.STRING) {
            throw new RuntimeException(DIRECTIVE_PREFIX+"outdir needs a string expression");
        }
        state.getRootFileInfo().setDestinationDir((String) name.getValue());
        return AfterProcessingBehaviour.PROCESSED;
    }
}
