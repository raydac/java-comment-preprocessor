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
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import static org.junit.Assert.*;

public class GlobalDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {

  private static final GlobalDirectiveHandler HANDLER = new GlobalDirectiveHandler();

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext context = executeGlobalPhase("directive_global.txt", null);
    assertTrue(context.containsGlobalVariable("xxx"));
    final Value var = context.findVariableForName("xxx");
    assertEquals(Long.valueOf(10), var.asLong());
  }

  @Test
  public void testExecution_PreprocessingPhase() throws Exception {
    final PreprocessorContext context = assertFilePreprocessing("directive_global.txt", false, null, null);
    assertFalse(context.containsGlobalVariable("xxx"));
    assertNull(context.findVariableForName("xxx"));
  }

  @Test
  public void testExecution_WrongCases() throws Exception {
    assertGlobalPhaseException("\n\n//#global 23123", 3, null);
    assertGlobalPhaseException("\n\n//#global", 3, null);
    assertGlobalPhaseException("\n\n//#global ", 3, null);
    assertGlobalPhaseException("\n\n//#global hh=", 3, null);
    assertGlobalPhaseException("\n\n//#global xx==10", 3, null);
    assertGlobalPhaseException("\n\n//#global =10", 3, null);
    assertGlobalPhaseException("\n\n//#global ====", 3, null);
  }

  @Override
  public void testExecutionCondition() throws Exception {
    assertTrue(HANDLER.executeOnlyWhenExecutionAllowed());
  }

  @Override
  public void testKeyword() throws Exception {
    assertEquals("global", HANDLER.getName());
  }

  @Override
  public void testPhase() throws Exception {
    assertTrue(HANDLER.isGlobalPhaseAllowed());
    assertFalse(HANDLER.isPreprocessingPhaseAllowed());
  }

  @Override
  public void testReference() throws Exception {
    assertReference(HANDLER);
  }

  @Override
  public void testArgumentType() throws Exception {
    assertEquals(DirectiveArgumentType.SET, HANDLER.getArgumentType());
  }
}
