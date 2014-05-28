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
package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import org.mockito.Mockito;
import static org.junit.Assert.*;

public class OutCharsetHandlerTest extends AbstractCommandLineHandlerTest {

  private static final OutCharsetHandler HANDLER = new OutCharsetHandler();

  @Override
  public void testThatTheHandlerInTheHandlerList() {
    assertHandlerInTheHandlerList(HANDLER);
  }

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext mock = Mockito.mock(PreprocessorContext.class);
    assertFalse(HANDLER.processCommandLineKey(null, mock));
    assertFalse(HANDLER.processCommandLineKey("/o:UUU", mock));
    assertFalse(HANDLER.processCommandLineKey("/TT:", mock));
    assertFalse(HANDLER.processCommandLineKey("/tT", mock));
    assertTrue(HANDLER.processCommandLineKey("/tt:HELLOWORLD", mock));
    Mockito.verify(mock).setOutCharacterEncoding("HELLOWORLD");

    Mockito.reset(mock);

    assertTrue(HANDLER.processCommandLineKey("/TT:NEW", mock));
    Mockito.verify(mock).setOutCharacterEncoding("NEW");
  }

  @Override
  public void testName() {
    assertEquals("/TT:", HANDLER.getKeyName());
  }

  @Override
  public void testDescription() {
    assertDescription(HANDLER);
  }
}
