package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.junit.Assert.*;

public class LocalDirectiveHandlerTest extends AbstractDirectiveHandlerTest {

    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext context = assertPreprocessing("directive_local.txt",null);
        
        assertEquals(Long.valueOf(5), context.getLocalVariable("x").asLong());
        assertEquals(Long.valueOf(10), context.getLocalVariable("y").asLong());
        assertEquals(Long.valueOf(15), context.getLocalVariable("z").asLong());
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("local",new LocalDirectiveHandler().getName());
    }
    
    @Override
    public void testHasExpression() throws Exception {
        assertTrue(new LocalDirectiveHandler().hasExpression());
    }

    @Override
    public void testReference() throws Exception {
        assertReference(new LocalDirectiveHandler());
    }
}
