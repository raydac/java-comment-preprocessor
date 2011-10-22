package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import com.igormaznitsa.jcpreprocessor.references.IncludeReference;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class IncludeDirectiveHandler  extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "include";
    }

    @Override
    public boolean hasExpression() {
        return true;
    }

    @Override
    public DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext context) throws IOException {
         if (state.isOutEnabled()) {
                    // include a file with the path to the place (with processing)
                    Value p_value = Expression.eval(string,context);
                    
                    if (p_value == null || p_value.getType() != ValueType.STRING) {
                        throw new IOException("You don't have a string result in the #include instruction");
                    }
                    
                    IncludeReference p_inRef = new IncludeReference(state.getCurrentProcessingFile(), state);
                    state.pushIncludeReference(p_inRef);
                    String s_fName = (String) p_value.getValue();
                    try {
                        File p_inclFile = null;
                        p_inclFile = context.getSourceFile(s_fName);
                        
                        state.setCurrentProcessingFile(p_inclFile);
                        state.setStrings(PreprocessorUtils.readTextFileAndAddNullAtEnd(p_inclFile, context.getCharacterEncoding()));
                    } catch (FileNotFoundException e) {
                        throw new IOException("You have got the bad file pointer in the #include instruction [" + s_fName + "]");
                    }
                    state.pushFileName(state.getCurrentFileCanonicalPath());
                    state.setCurrentFileCanonicalPath(s_fName);
                    state.setCurrentStringIndex(0);
                }
         return DirectiveBehaviour.NORMAL;
    }
 
    
}
