package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.Test;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import static org.junit.Assert.*;

public class LocalDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {

    @Override
    public void testExecution() throws Exception {
        final PreprocessorContext context = assertFilePreprocessing("directive_local.txt", null);

        assertEquals(Long.valueOf(5), context.getLocalVariable("x").asLong());
        assertEquals(Long.valueOf(10), context.getLocalVariable("y").asLong());
        assertEquals(Long.valueOf(15), context.getLocalVariable("z").asLong());
    }

    @Override
    public void testExecutionCondition() throws Exception {
        assertTrue(new LocalDirectiveHandler().executeOnlyWhenExecutionAllowed());
    }

    @Test
    public void testExecution_ExceptionOnExpressionAbsence() {
        assertPreprocessorException("1\n2\n   //#local \n3", 3);
        assertPreprocessorException("1\n2\n   //#local\n3", 3);
    }

    @Test
    public void testExecution_ExceptionOnWrongExpression() {
        assertPreprocessorException("1\n2\n   //#local 3\n3", 3);
        assertPreprocessorException("1\n2\n   //#local a=\n3", 3);
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
