package com.igormaznitsa.jcpreprocessor.directives;

import org.junit.Test;
import static org.junit.Assert.*;

public class PrefixPostfixDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {
    private static final PrefixDirectiveHandler HANDLER_PREFIX = new PrefixDirectiveHandler();
    private static final PostfixDirectiveHandler HANDLER_POSTFIX = new PostfixDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_prefixpostfix.txt", null, null);
    }

    @Test
    public void testPrefix_wrongArgument() {
        assertPreprocessorException("\n    //#prefix -", 2, null);
        assertPreprocessorException("\n    //#prefix-1", 2, null);
        assertPreprocessorException("\n //#prefixa", 2, null);
    }
    
    @Test
    public void testPostfix_wrongArgument() {
        assertPreprocessorException("\n   //#postfix -", 2, null);
        assertPreprocessorException("\n //#postfix1", 2, null);
        assertPreprocessorException("\n //#postfix+q", 2, null);
    }
    
    @Override
    public void testKeyword() throws Exception {
        assertEquals("prefix", HANDLER_PREFIX.getName());
        assertEquals("postfix", HANDLER_POSTFIX.getName());
    }

    @Override
    public void testExecutionCondition() throws Exception {
        assertTrue(HANDLER_PREFIX.executeOnlyWhenExecutionAllowed());
        assertTrue(HANDLER_POSTFIX.executeOnlyWhenExecutionAllowed());
    }

    @Override
    public void testReference() throws Exception {
        assertReference(HANDLER_PREFIX);
        assertReference(HANDLER_POSTFIX);
    }

    @Override
    public void testPhase() throws Exception {
        assertTrue(HANDLER_POSTFIX.isPreprocessingPhaseAllowed());
        assertFalse(HANDLER_POSTFIX.isGlobalPhaseAllowed());
        assertTrue(HANDLER_PREFIX.isPreprocessingPhaseAllowed());
        assertFalse(HANDLER_PREFIX.isGlobalPhaseAllowed());
    }
    
    @Override
    public void testArgumentType() throws Exception {
        assertEquals(DirectiveArgumentType.ONOFF, HANDLER_POSTFIX.getArgumentType());
        assertEquals(DirectiveArgumentType.ONOFF, HANDLER_PREFIX.getArgumentType());
    }
}
