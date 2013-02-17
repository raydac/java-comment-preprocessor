package com.igormaznitsa.jcp.directives;

// This test checks work of //$$, //$ and /*-*/
// Those directives are very specific and they don't have any distinguished handler
public class SpecialDirectivesTest extends AbstractDirectiveHandlerAcceptanceTest {

    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_special.txt", false, null, null);
    }

    @Override
    public void testKeyword() throws Exception {
        // do nothing because it is a group test
    }

    @Override
    public void testExecutionCondition() throws Exception {
        // do nothing because it is a group test
    }

    @Override
    public void testReference() throws Exception {
        // do nothing because it is a group test
    }

    @Override
    public void testArgumentType() throws Exception {
        // do nothing because it is a group test
    }

    @Override
    public void testPhase() throws Exception {
        // do nothing because it is a group test
    }
    
}
