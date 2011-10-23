package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import com.igormaznitsa.jcpreprocessor.references.IncludeReference;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.File;
import java.io.FileNotFoundException;
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
    public DirectiveBehaviourEnum execute(String string, ParameterContainer state, PreprocessorContext context) {
        if (state.isOutEnabled()) {
            // include a file with the path to the place (with processing)
            Value p_value = Expression.eval(string, context);

            if (p_value == null || p_value.getType() != ValueType.STRING) {
                throw new RuntimeException("//#include needs a string expression");
            }

            IncludeReference p_inRef = new IncludeReference(state.getCurrentProcessingFile(), state);
            state.pushIncludeReference(p_inRef);
            String s_fName = (String) p_value.getValue();

            File p_inclFile = null;
            try {
                p_inclFile = context.getSourceFile(s_fName);
            } catch (IOException ex) {
                throw new RuntimeException("Can't find source file", ex);
            }

            state.setCurrentProcessingFile(p_inclFile);
            try {
                state.setStrings(PreprocessorUtils.readTextFileAndAddNullAtEnd(p_inclFile, context.getCharacterEncoding()));
            } catch (IOException ex) {
                throw new RuntimeException("Can't read whole file for an exception", ex);
            }

            state.pushFileName(state.getCurrentFileCanonicalPath());
            state.setCurrentFileCanonicalPath(s_fName);
            state.setCurrentStringIndex(0);
        }
        return DirectiveBehaviourEnum.PROCESSED;
    }
}
