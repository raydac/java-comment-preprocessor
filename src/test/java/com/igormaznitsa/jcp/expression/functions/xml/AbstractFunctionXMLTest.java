package com.igormaznitsa.jcp.expression.functions.xml;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.functions.AbstractFunctionTest;
import java.io.File;
import org.junit.Before;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public abstract class AbstractFunctionXMLTest extends AbstractFunctionTest {
    protected PreprocessorContext SPY_CONTEXT;
    protected Value OPENED_DOCUMENT_ID;
    protected Value OPENED_DOCUMENT_ROOT;
    
    @Before
    public void initTest() throws Exception {
        SPY_CONTEXT = spy(new PreprocessorContext());
        final File thisRoot = new File(this.getClass().getResource("./").toURI());
        
        doAnswer(new Answer(){
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                final String name = (String)invocation.getArguments()[0];
                return new File(thisRoot,name);
            }
        }).when(SPY_CONTEXT).getSourceFile(any(String.class));
        
        OPENED_DOCUMENT_ID = new FunctionXML_OPEN().executeStr(SPY_CONTEXT, Value.valueOf("test.xml"));
        OPENED_DOCUMENT_ROOT = new FunctionXML_GETROOT().executeStr(SPY_CONTEXT, OPENED_DOCUMENT_ID);
    }
}
