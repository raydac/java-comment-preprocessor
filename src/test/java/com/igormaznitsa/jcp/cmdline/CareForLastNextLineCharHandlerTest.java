/*
 * Copyright 2014 Igor Maznitsa.
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

public class CareForLastNextLineCharHandlerTest extends AbstractCommandLineHandlerTest{

  private static final CareForLastNextLineCharHandler HANDLER = new CareForLastNextLineCharHandler();

  @Override
  public void testThatTheHandlerInTheHandlerList() {
    assertHandlerInTheHandlerList(HANDLER);
  }

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext mock = mock(PreprocessorContext.class);

    assertFalse(HANDLER.processCommandLineKey("/n:", mock));
    assertFalse(HANDLER.processCommandLineKey("/NN", mock));
    assertFalse(HANDLER.processCommandLineKey("/N ", mock));
    verify(mock, never()).setClearDestinationDirBefore(anyBoolean());

    assertTrue(HANDLER.processCommandLineKey("/N", mock));
    verify(mock).setCareForLastNextLine(true);
    reset(mock);

    assertTrue(HANDLER.processCommandLineKey("/n", mock));
    verify(mock).setCareForLastNextLine(true);
    reset(mock);
  }

  @Override
  public void testName() {
    assertEquals("/N", HANDLER.getKeyName());
  }

  @Override
  public void testDescription() {
    assertDescription(HANDLER);
  }
  
}
