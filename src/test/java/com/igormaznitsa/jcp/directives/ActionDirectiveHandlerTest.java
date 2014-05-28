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

import org.mockito.Mockito;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ActionDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {

  private static final ActionDirectiveHandler HANDLER = new ActionDirectiveHandler();

  @Test
  @Override
  public void testExecution() throws Exception {
    final PreprocessorExtension mockup = mock(PreprocessorExtension.class);
    when(mockup.processAction(any(PreprocessorContext.class), any(Value[].class))).thenReturn(Boolean.TRUE);

    assertFilePreprocessing("directive_action.txt", false, mockup, null);

    final Value val1 = Value.valueOf(1L);
    final Value val2 = Value.valueOf(2L);
    final Value val3 = Value.valueOf(7L);
    final Value val4 = Value.valueOf(11L);
    final Value val5 = Value.valueOf(Boolean.TRUE);
    final Value val6 = Value.valueOf("hello,");

    verify(mockup).processAction(any(PreprocessorContext.class), eq(new Value[]{val1, val2, val3, val4, val5, val6}));
  }

  @Test
  public void testExecutionWrongExpression() {
    final PreprocessorExtension mock = Mockito.mock(PreprocessorExtension.class);

    assertPreprocessorException("\n//#action", 2, mock);
    assertPreprocessorException("\n//#action illegal_variable", 2, mock);
    assertPreprocessorException("\n//#actionno_space", 2, mock);
    assertPreprocessorException("\n//#action 1,2,3,4,,5", 2, mock);
    assertPreprocessorException("\n//#action 1,2,3,4,", 2, mock);
  }

  @Test
  @Override
  public void testKeyword() throws Exception {
    assertEquals("action", HANDLER.getName());
  }

  @Test
  @Override
  public void testExecutionCondition() throws Exception {
    assertTrue(HANDLER.executeOnlyWhenExecutionAllowed());
  }

  @Test
  @Override
  public void testReference() throws Exception {
    assertReference(HANDLER);
  }

  @Override
  public void testArgumentType() throws Exception {
    assertEquals(DirectiveArgumentType.MULTIEXPRESSION, HANDLER.getArgumentType());
  }

  @Test
  @Override
  public void testPhase() throws Exception {
    assertFalse(HANDLER.isGlobalPhaseAllowed());
    assertTrue(HANDLER.isPreprocessingPhaseAllowed());
  }
}
