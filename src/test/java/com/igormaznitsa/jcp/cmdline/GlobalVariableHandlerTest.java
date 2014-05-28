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
import com.igormaznitsa.jcp.expression.Value;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GlobalVariableHandlerTest extends AbstractCommandLineHandlerTest {

  private static final GlobalVariableHandler HANDLER = new GlobalVariableHandler();

  @Override
  public void testThatTheHandlerInTheHandlerList() {
    assertHandlerInTheHandlerList(HANDLER);
  }

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext mock = mock(PreprocessorContext.class);

    assertFalse(HANDLER.processCommandLineKey(null, mock));
    assertFalse(HANDLER.processCommandLineKey("", mock));
    assertFalse(HANDLER.processCommandLineKey("/p", mock));
    assertFalse(HANDLER.processCommandLineKey("/pp:", mock));
    assertFalse(HANDLER.processCommandLineKey("/P:", mock));

    assertTrue(HANDLER.processCommandLineKey("/P:hello=0", mock));

    verify(mock).setGlobalVariable("hello", Value.INT_ZERO);
  }

  @Override
  public void testName() {
    assertEquals("/P:", HANDLER.getKeyName());
  }

  @Override
  public void testDescription() {
    assertDescription(HANDLER);
  }

}
