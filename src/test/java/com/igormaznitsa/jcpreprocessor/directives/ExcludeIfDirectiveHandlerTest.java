package com.igormaznitsa.jcpreprocessor.directives;

import java.util.List;
import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState.ExcludeIfInfo;
import static org.junit.Assert.*;

public class ExcludeIfDirectiveHandlerTest extends AbstractDirectiveHandlerIntegrationTest {

    private static final ExcludeIfDirectiveHandler HANDLER = new ExcludeIfDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        final List<ExcludeIfInfo> list = executeGlobalPhase("directive_excludeif.txt");
        assertEquals("Must be two //#excludeif ",list.size(), 2);
        final ExcludeIfInfo info1 = list.get(1);
        final ExcludeIfInfo info2 = list.get(0);
        
        assertEquals("true",info1.getCondition());
        assertEquals(2,info1.getStringIndex());
        assertNotNull(info1.getFileInfoContainer());
        
        assertEquals("hello+world",info2.getCondition());
        assertEquals(6,info2.getStringIndex());
        assertNotNull(info2.getFileInfoContainer());
    }

    @Override
    public void testKeyword() throws Exception {
        assertEquals("excludeif",HANDLER.getName());
    }

    @Override
    public void testHasExpression() throws Exception {
        assertTrue(HANDLER.hasExpression());
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
        assertTrue(HANDLER.isGlobalPhaseAllowed());
        assertFalse(HANDLER.isPreprocessingPhaseAllowed());
    }
}
