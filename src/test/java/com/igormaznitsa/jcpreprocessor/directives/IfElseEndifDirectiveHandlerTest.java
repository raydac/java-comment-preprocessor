package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.Test;
import static org.junit.Assert.*;

public class IfElseEndifDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {

    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_if_else_endif.txt", null);
    }

    @Test
    public void testIf_ExceptionWithoutExpression() throws Exception {
        assertPreprocessorException("\n\n\n   \n  //#if   \ntest\n  //#endif", 5);
        assertPreprocessorException("\n\n\n   \n  //#if\ntest\n  //#endif", 5);
    }

    @Test
    public void testIf_ExceptionWithoutEndIf() throws Exception {
        assertPreprocessorException("\n\n\n   \n  //#if true\n\n", 5);
        assertPreprocessorException("\n\n\n   \n  //#if true\n//#if true\n//#endif\n", 5);
    }

    @Test
    public void testElse_ExeptionWithoutIf() throws Exception {
        assertPreprocessorException("\n\n\n   \n  //#else  \ntest\n  //#endif", 5);
    }

    @Test
    public void testEndIf_ExceptionWithoutIf() throws Exception {
        assertPreprocessorException("\n\n\n   \n  //#endif", 5);
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("if",new IfDirectiveHandler().getName());
        assertEquals("else",new ElseDirectiveHandler().getName());
        assertEquals("endif",new EndIfDirectiveHandler().getName());
    }

    @Override
    public void testHasExpression() throws Exception {
        assertTrue(new IfDirectiveHandler().hasExpression());
        assertFalse(new ElseDirectiveHandler().hasExpression());
        assertFalse(new EndIfDirectiveHandler().hasExpression());
    }

    @Override
    public void testExecutionCondition() throws Exception {
        assertFalse(new IfDirectiveHandler().executeOnlyWhenExecutionAllowed());
        assertFalse(new ElseDirectiveHandler().executeOnlyWhenExecutionAllowed());
        assertFalse(new EndIfDirectiveHandler().executeOnlyWhenExecutionAllowed());
    }

    @Override
    public void testReference() throws Exception {
        assertReference(new IfDirectiveHandler());
        assertReference(new ElseDirectiveHandler());
        assertReference(new EndIfDirectiveHandler());
    }
    
}
