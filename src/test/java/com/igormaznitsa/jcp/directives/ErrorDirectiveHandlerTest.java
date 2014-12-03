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
package com.igormaznitsa.jcp.directives;

import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import org.junit.Test;

public class ErrorDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {

  private static final ErrorDirectiveHandler HANDLER = new ErrorDirectiveHandler();

  @Override
  public void testExecution() throws Exception {
    final PreprocessorLogger mock = Mockito.mock(PreprocessorLogger.class);
    try {
      assertFilePreprocessing("directive_error.txt", false, null, mock);
    }
    catch (Exception ex) {
      Mockito.verify(mock).error("string2");
      final PreprocessorException pp = PreprocessorException.extractPreprocessorException(ex);
      assertEquals(2, pp.getIncludeChain()[0].getStringIndex());
    }
  }

  @Test
  public void testExecution_wrongCases() {
    assertPreprocessorException("\n\n//#error 324444444444987987987982374987294873294324324\n", 3, null);
    assertPreprocessorException("\n\n//#error sjdasd\n", 3, null);
    assertPreprocessorException("\n\n//#error \n", 3, null);
  }

  @Override
  public void testKeyword() throws Exception {
    assertEquals("error", HANDLER.getName());
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
  public void testArgumentType() throws Exception {
    assertEquals(DirectiveArgumentType.EXPRESSTION, HANDLER.getArgumentType());
  }

  @Override
  public void testPhase() throws Exception {
    assertTrue(HANDLER.isPreprocessingPhaseAllowed());
    assertFalse(HANDLER.isGlobalPhaseAllowed());
  }
}
