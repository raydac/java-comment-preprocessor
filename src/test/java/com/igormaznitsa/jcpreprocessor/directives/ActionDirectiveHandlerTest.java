package com.igormaznitsa.jcpreprocessor.directives;

import org.mockito.Mockito;
import com.igormaznitsa.jcpreprocessor.context.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.extension.PreprocessorExtension;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ActionDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {
    private static final ActionDirectiveHandler HANDLER = new ActionDirectiveHandler();
    
    @Test
    @Override
    public void testExecution() throws Exception {
        final PreprocessorExtension mockup = mock(PreprocessorExtension.class);
        when(mockup.processAction(any(PreprocessorContext.class),any(Value[].class))).thenReturn(Boolean.TRUE);
        
        assertFilePreprocessing("directive_action.txt", mockup, null);

        final Value val1 = Value.valueOf(1L);
        final Value val2 = Value.valueOf(2L);
        final Value val3 = Value.valueOf(7L);
        final Value val4 = Value.valueOf(11L);
        final Value val5 = Value.valueOf(Boolean.TRUE);
        final Value val6 = Value.valueOf("hello,");
        
        verify(mockup).processAction(any(PreprocessorContext.class),eq(new Value[]{val1,val2,val3,val4,val5,val6}));
    }

    @Test
    public void testExecutionWrongExpression() {
        final PreprocessorExtension mock = Mockito.mock(PreprocessorExtension.class);
        
        assertPreprocessorException("\n//#action", 2, mock);
        assertPreprocessorException("\n//#action illegal_variable", 2, mock);
        assertPreprocessorException("\n//#actionno_space", 2, mock);
        assertPreprocessorException("\n//#action 1,2,3,4,,5", 2, mock);
        assertPreprocessorException("\n//#action 1,2,3,4,", 2, mock);
    }
    
    @Test
    @Override
    public void testKeyword() throws Exception {
        assertEquals("action", HANDLER.getName());
    }

    @Test
    @Override
    public void testExecutionCondition() throws Exception {
        assertTrue(HANDLER.executeOnlyWhenExecutionAllowed());
    }

    @Test
    @Override
    public void testReference() throws Exception {
        assertReference(HANDLER);
    }

    @Override
    public void testArgumentType() throws Exception {
        assertEquals(DirectiveArgumentType.MULTIEXPRESSION, HANDLER.getArgumentType());
    }

    @Test
    @Override
    public void testPhase() throws Exception {
        assertFalse(HANDLER.isGlobalPhaseAllowed());
        assertTrue(HANDLER.isPreprocessingPhaseAllowed());
    }
}
