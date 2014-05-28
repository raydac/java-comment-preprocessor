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
import static org.junit.Assert.*;

public class WhileContinueBreakEndDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {

  private static final WhileDirectiveHandler WHILE_HANDLER = new WhileDirectiveHandler();
  private static final ContinueDirectiveHandler CONTINUE_HANDLER = new ContinueDirectiveHandler();
  private static final BreakDirectiveHandler BREAK_HANDLER = new BreakDirectiveHandler();
  private static final EndDirectiveHandler END_HANDLER = new EndDirectiveHandler();

  @Override
  public void testExecution() throws Exception {
    assertFilePreprocessing("directive_while_continue_break_end.txt", false, null, null);
  }

  @Test
  public void testWhile_ExceptionWithoutExpression() throws Exception {
    assertPreprocessorException("\n\n\n   \n\n  //#while   \ntest\n  //#end", 6, null);
  }

  @Test
  public void testWhile_ExceptionForNonBooleanExpression() throws Exception {
    assertPreprocessorException("\n\n\n   \n\n  //#while 234  \ntest\n  //#end", 6, null);
  }

  @Test
  public void testWhile_ExceptionForNonClosed() throws Exception {
    assertPreprocessorException("\n\n\n   \n\n  //#while true  \ntest\n", 6, null);
  }

  @Test
  public void testBreak_ExceptionWithoutWhile() throws Exception {
    assertPreprocessorException("\n\n\n   \n\n  //#break \ntest\n", 6, null);
  }

  @Test
  public void testContinue_ExceptionWithoutWhile() throws Exception {
    assertPreprocessorException("\n\n\n   \n\n  //#continue \ntest\n", 6, null);
  }

  @Test
  public void testEnd_ExceptionWithoutWhile() throws Exception {
    assertPreprocessorException("\n\n\n   \n\n  //#end \ntest\n", 6, null);
  }

  @Override
  public void testKeyword() throws Exception {
    assertEquals("while", WHILE_HANDLER.getName());
    assertEquals("break", BREAK_HANDLER.getName());
    assertEquals("continue", CONTINUE_HANDLER.getName());
    assertEquals("end", END_HANDLER.getName());
  }

  @Override
  public void testExecutionCondition() throws Exception {
    assertFalse(WHILE_HANDLER.executeOnlyWhenExecutionAllowed());
    assertTrue(BREAK_HANDLER.executeOnlyWhenExecutionAllowed());
    assertTrue(CONTINUE_HANDLER.executeOnlyWhenExecutionAllowed());
    assertFalse(END_HANDLER.executeOnlyWhenExecutionAllowed());
  }

  @Override
  public void testReference() throws Exception {
    assertReference(WHILE_HANDLER);
    assertReference(BREAK_HANDLER);
    assertReference(CONTINUE_HANDLER);
    assertReference(END_HANDLER);
  }

  @Override
  public void testPhase() throws Exception {
    assertFalse(WHILE_HANDLER.isGlobalPhaseAllowed());
    assertFalse(BREAK_HANDLER.isGlobalPhaseAllowed());
    assertFalse(CONTINUE_HANDLER.isGlobalPhaseAllowed());
    assertFalse(END_HANDLER.isGlobalPhaseAllowed());

    assertTrue(WHILE_HANDLER.isPreprocessingPhaseAllowed());
    assertTrue(BREAK_HANDLER.isPreprocessingPhaseAllowed());
    assertTrue(CONTINUE_HANDLER.isPreprocessingPhaseAllowed());
    assertTrue(END_HANDLER.isPreprocessingPhaseAllowed());
  }

  @Override
  public void testArgumentType() throws Exception {
    assertEquals(DirectiveArgumentType.BOOLEAN, WHILE_HANDLER.getArgumentType());
    assertEquals(DirectiveArgumentType.NONE, BREAK_HANDLER.getArgumentType());
    assertEquals(DirectiveArgumentType.NONE, CONTINUE_HANDLER.getArgumentType());
    assertEquals(DirectiveArgumentType.NONE, END_HANDLER.getArgumentType());
  }
}
