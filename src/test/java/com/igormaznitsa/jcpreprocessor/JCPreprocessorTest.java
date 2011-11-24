package com.igormaznitsa.jcpreprocessor;

import java.io.File;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.exceptions.PreprocessorException;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import org.junit.Test;
import static org.junit.Assert.*;

public final class JCPreprocessorTest {
    
    private final void assertGVDFPreprocessorException(final String file, final int stringIndex) throws Exception {
        final PreprocessorContext context = new PreprocessorContext();
        context.addGlobalVarDefiningFile(new File(this.getClass().getResource(file).toURI()));
        final JCPreprocessor preprocessor = new JCPreprocessor(context);
        try {
            preprocessor.processGlobalVarDefiningFiles();
            fail("Must throw a PreprocessorException");
        }catch(PreprocessorException expected){
            if (stringIndex!=expected.getStringIndex()){
                fail("Wrong error string index ["+expected.toString()+']');
            }
        }
    }
    
    @Test
    public void testProcessGlobalVarDefiningFiles() throws Exception {
        final PreprocessorContext context = new PreprocessorContext();
        context.addGlobalVarDefiningFile(new File(this.getClass().getResource("./global_ok.txt").toURI()));
        final JCPreprocessor preprocessor = new JCPreprocessor(context);
        preprocessor.processGlobalVarDefiningFiles();
        
        assertEquals("Must have the variable", "hello world",context.findVariableForName("globalVar1").asString());
        assertEquals("Must have the variable", Value.INT_THREE, context.findVariableForName("globalVar2"));
        assertEquals("Character input encoding must be changed", "ISO-8859-1", context.getInCharacterEncoding());
    }

    @Test
    public void testProcessGlobalVarDefiningFiles_ATsymbol() throws Exception {
        assertGVDFPreprocessorException("global_error_at.txt", 8);
    }
}
