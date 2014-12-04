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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KeepLineHandlerTest extends AbstractCommandLineHandlerTest {

  private static final KeepLineHandler HANDLER = new KeepLineHandler();

  @Override
  public void testThatTheHandlerInTheHandlerList() {
    assertHandlerInTheHandlerList(HANDLER);
  }

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext mock = mock(PreprocessorContext.class);

    assertFalse(HANDLER.processCommandLineKey("/k:", mock));
    assertFalse(HANDLER.processCommandLineKey("/KK", mock));
    assertFalse(HANDLER.processCommandLineKey("/K ", mock));
    verify(mock, never()).setKeepLines(anyBoolean());

    assertTrue(HANDLER.processCommandLineKey("/K", mock));
    verify(mock).setKeepLines(true);
    reset(mock);

    assertTrue(HANDLER.processCommandLineKey("/k", mock));
    verify(mock).setKeepLines(true);
    reset(mock);
  }

  @Override
  public void testName() {
    assertEquals("/K", HANDLER.getKeyName());
  }

  @Override
  public void testDescription() {
    assertDescription(HANDLER);
  }
}
