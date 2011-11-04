package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.Test;
import static org.junit.Assert.*;

public class IncludeDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {

    private static final IncludeDirectiveHandler HANDLER = new IncludeDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
    }

    @Test
    public void testExecution_wrongCases() throws Exception {
        assertPreprocessorException("\n\n\n//#include 111\n", 4, null);
        assertPreprocessorException("\n\n\n//#include\n", 4, null);
        assertPreprocessorException("\n\n\n//#include \n", 4, null);
        assertPreprocessorException("\n\n\n//#include =\n", 4, null);
        assertPreprocessorException("\n\n\n//#include=\n", 4, null);
        assertPreprocessorException("\n\n\n//#include333\n", 4, null);
        assertPreprocessorException("\n\n\n//#include true\n", 4, null);
        assertPreprocessorException("\n\n\n//#include \"/some/nonexist/absolutnonexist/file.ttxt\"\n", 4, null);
    }
    
    @Override
    public void testKeyword() throws Exception {
        assertEquals("include", HANDLER.getName());
    }

    @Override
    public void testExecutionCondition() throws Exception {
        assertTrue(HANDLER.executeOnlyWhenExecutionAllowed());
    }

    @Override
    public void testReference() throws Exception {
        assertReference(HANDLER);
    }

    @Override
    public void testPhase() throws Exception {
        assertFalse(HANDLER.isGlobalPhaseAllowed());
        assertTrue(HANDLER.isPreprocessingPhaseAllowed());
    }

    @Override
    public void testArgumentType() throws Exception {
        assertEquals(DirectiveArgumentType.STRING, HANDLER.getArgumentType());
    }
}
