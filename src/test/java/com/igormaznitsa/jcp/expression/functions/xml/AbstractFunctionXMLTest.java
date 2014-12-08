/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    doAnswer(new Answer<Object>() {
      @Override
      public Object answer(final InvocationOnMock invocation) throws Throwable {
        final String name = (String) invocation.getArguments()[0];
        return new File(thisRoot, name);
      }
    }).when(SPY_CONTEXT).getSourceFile(any(String.class));

    OPENED_DOCUMENT_ID = new FunctionXML_OPEN().executeStr(SPY_CONTEXT, Value.valueOf("test.xml"));
    OPENED_DOCUMENT_ROOT = new FunctionXML_ROOT().executeStr(SPY_CONTEXT, OPENED_DOCUMENT_ID);
  }
}
