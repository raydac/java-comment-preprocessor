package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.ParameterContainer;
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
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext context) {
        final Value includedFilePath = Expression.eval(string, context);

        if (includedFilePath == null || includedFilePath.getType() != ValueType.STRING) {
            throw new RuntimeException("//#include needs a string expression as a file path");
        }

        try {
            state.openFile(context.getSourceFile(includedFilePath.asString()));
        }catch(IOException ex){
            
        }
        return DirectiveBehaviour.PROCESSED;
    }
}
