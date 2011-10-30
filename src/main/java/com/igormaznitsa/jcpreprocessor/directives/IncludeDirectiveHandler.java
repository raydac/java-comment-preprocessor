package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.io.IOException;

public class IncludeDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "include";
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
    public String getExpressionType() {
        return "STRING";
    }
    
    @Override
    public AfterProcessingBehaviour execute(String string, PreprocessingState state, PreprocessorContext context) {
        final Value includedFilePath = Expression.eval(string, context,state);

        if (includedFilePath == null || includedFilePath.getType() != ValueType.STRING) {
            throw new RuntimeException(DIRECTIVE_PREFIX+"include needs a string expression as a file path");
        }

        try {
            state.openFile(context.getSourceFile(includedFilePath.asString()));
        }catch(IOException ex){
            throw new RuntimeException("Can't open a file to include ["+includedFilePath.asString()+']',ex);
        }
        return AfterProcessingBehaviour.PROCESSED;
    }
}
