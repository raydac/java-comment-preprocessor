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

import org.junit.Test;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import static org.junit.Assert.*;

public class GlobalIfElseEndifTest extends AbstractDirectiveHandlerAcceptanceTest {

  private static final GlobalIfDirectiveHandler HANDLER_GLOBAL_IF = new GlobalIfDirectiveHandler();
  private static final GlobalElseDirectiveHandler HANDLER_GLOBAL_ELSE = new GlobalElseDirectiveHandler();
  private static final GlobalEndIfDirectiveHandler HANDLER_GLOBAL_ENDIF = new GlobalEndIfDirectiveHandler();

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext context = executeGlobalPhase("directive_globalifelseendif.txt", null);
    assertTrue(context.containsGlobalVariable("expected"));
    assertFalse(context.containsGlobalVariable("unexpected"));
    assertEquals(Boolean.TRUE, context.findVariableForName("expected").asBoolean());
  }

  @Test
  public void testExecution_PreprocessingPhase() throws Exception {
    assertFilePreprocessing("directive_globalifelseendif.txt", false, null, null);
    assertFilePreprocessing("directive_globalifelseendif2.txt", false, null, null);
  }

  @Test
  public void testExecution_wrongCases() throws Exception {
    assertGlobalPhaseException("\n//#_if true", 2, null);
    assertGlobalPhaseException("\n//#_if true\n//#_else", 2, null);
    assertGlobalPhaseException("\n//#_if true\n//#_if true\n//#_else\n//#_endif", 2, null);
    assertGlobalPhaseException("\n//#_if true\n//#_if 111\n//#_else\n//#_endif", 3, null);
    assertGlobalPhaseException("\n//#_endif", 2, null);
    assertGlobalPhaseException("\n//#_else", 2, null);
    assertGlobalPhaseException("\n//#_else\n//#_endif", 2, null);
    assertGlobalPhaseException("\n//#_if xxx\n//#_endif", 2, null);
    assertGlobalPhaseException("\n//#global xxx=1\n//#_if xxx\n//#_endif", 3, null);
  }

  @Override
  public void testKeyword() throws Exception {
    assertEquals("_if", HANDLER_GLOBAL_IF.getName());
    assertEquals("_else", HANDLER_GLOBAL_ELSE.getName());
    assertEquals("_endif", HANDLER_GLOBAL_ENDIF.getName());
  }

  @Override
  public void testExecutionCondition() throws Exception {
    assertFalse(HANDLER_GLOBAL_IF.executeOnlyWhenExecutionAllowed());
    assertFalse(HANDLER_GLOBAL_ELSE.executeOnlyWhenExecutionAllowed());
    assertFalse(HANDLER_GLOBAL_ENDIF.executeOnlyWhenExecutionAllowed());
  }

  @Override
  public void testReference() throws Exception {
    assertReference(HANDLER_GLOBAL_IF);
    assertReference(HANDLER_GLOBAL_ELSE);
    assertReference(HANDLER_GLOBAL_ENDIF);
  }

  @Override
  public void testPhase() throws Exception {
    assertTrue(HANDLER_GLOBAL_IF.isGlobalPhaseAllowed());
    assertFalse(HANDLER_GLOBAL_IF.isPreprocessingPhaseAllowed());

    assertTrue(HANDLER_GLOBAL_ELSE.isGlobalPhaseAllowed());
    assertFalse(HANDLER_GLOBAL_ELSE.isPreprocessingPhaseAllowed());

    assertTrue(HANDLER_GLOBAL_ENDIF.isGlobalPhaseAllowed());
    assertFalse(HANDLER_GLOBAL_ENDIF.isPreprocessingPhaseAllowed());
  }

  @Override
  public void testArgumentType() throws Exception {
    assertEquals(DirectiveArgumentType.BOOLEAN, HANDLER_GLOBAL_IF.getArgumentType());
    assertEquals(DirectiveArgumentType.NONE, HANDLER_GLOBAL_ELSE.getArgumentType());
    assertEquals(DirectiveArgumentType.NONE, HANDLER_GLOBAL_ENDIF.getArgumentType());
  }
}
