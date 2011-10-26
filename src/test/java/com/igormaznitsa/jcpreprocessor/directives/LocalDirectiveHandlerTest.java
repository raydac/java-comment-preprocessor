package com.igormaznitsa.jcpreprocessor.directives;

import java.util.List;
import org.junit.Test;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.exceptions.PreprocessorException;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class LocalDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {

    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext context = assertPreprocessing("directive_local.txt", null);

        assertEquals(Long.valueOf(5), context.getLocalVariable("x").asLong());
        assertEquals(Long.valueOf(10), context.getLocalVariable("y").asLong());
        assertEquals(Long.valueOf(15), context.getLocalVariable("z").asLong());
    }

    @Override
    public void testProcessOnlyIfCanBeProcessed() throws Exception {
        assertTrue(new LocalDirectiveHandler().processOnlyIfCanBeProcessed());
    }

    @Test
    public void testExecution_ThereIsNotExpression() {
        try {
            final List<String> out = new ArrayList<String>();
            final PreprocessorContext context = preprocessString("1\n2\n   //#local\n3", out, null);
            fail("Must throw PreprocessorException");
        } catch (PreprocessorException ex) {
            assertEquals(3, ex.getStringIndex());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception");
        }
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("local", new LocalDirectiveHandler().getName());
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
