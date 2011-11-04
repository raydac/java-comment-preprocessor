package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.JCPSpecialVariables;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

public class OutNameDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "outname";
    }

    @Override
    public String getReference() {
        return "it allows to change the destination file name for preprocessed text (it can be read through "+JCPSpecialVariables.VAR_DEST_FILE_NAME+')';
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.STRING;
    }
    
    @Override
    public AfterProcessingBehaviour execute(final String string, final PreprocessingState state, final PreprocessorContext context) {
        final Value dirName = Expression.eval(string, context,state);

        if (dirName == null || dirName.getType() != ValueType.STRING) {
            throw new RuntimeException(DIRECTIVE_PREFIX+"outname needs a string expression");
        }
        state.getRootFileInfo().setDestinationName(dirName.asString());
        return AfterProcessingBehaviour.PROCESSED;
    }
}
