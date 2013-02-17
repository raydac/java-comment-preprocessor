package com.igormaznitsa.jcp.directives;

import org.junit.Test;
import static org.junit.Assert.*;

public class IfElseEndifDirectiveWithKeepLinesHandlerTest extends IfElseEndifDirectiveHandlerTest {

    private static final IfDirectiveHandler IF_HANDLER = new IfDirectiveHandler();
    private static final ElseDirectiveHandler ELSE_HANDLER = new ElseDirectiveHandler();
    private static final EndIfDirectiveHandler ENDIF_HANDLER = new EndIfDirectiveHandler();
    
    @Override
    public void testExecution() throws Exception {
        assertFilePreprocessing("directive_if_else_endif_with_keptlines.txt", true, null, null);
    }
}
