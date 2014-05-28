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

public class PrefixPostfixDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {

  private static final PrefixDirectiveHandler HANDLER_PREFIX = new PrefixDirectiveHandler();
  private static final PostfixDirectiveHandler HANDLER_POSTFIX = new PostfixDirectiveHandler();

  @Override
  public void testExecution() throws Exception {
    assertFilePreprocessing("directive_prefixpostfix.txt", false, null, null);
  }

  @Test
  public void testPrefix_wrongArgument() {
    assertPreprocessorException("\n    //#prefix -", 2, null);
    assertPreprocessorException("\n    //#prefix-1", 2, null);
    assertPreprocessorException("\n //#prefixa", 2, null);
  }

  @Test
  public void testPostfix_wrongArgument() {
    assertPreprocessorException("\n   //#postfix -", 2, null);
    assertPreprocessorException("\n //#postfix1", 2, null);
    assertPreprocessorException("\n //#postfix+q", 2, null);
  }

  @Override
  public void testKeyword() throws Exception {
    assertEquals("prefix", HANDLER_PREFIX.getName());
    assertEquals("postfix", HANDLER_POSTFIX.getName());
  }

  @Override
  public void testExecutionCondition() throws Exception {
    assertTrue(HANDLER_PREFIX.executeOnlyWhenExecutionAllowed());
    assertTrue(HANDLER_POSTFIX.executeOnlyWhenExecutionAllowed());
  }

  @Override
  public void testReference() throws Exception {
    assertReference(HANDLER_PREFIX);
    assertReference(HANDLER_POSTFIX);
  }

  @Override
  public void testPhase() throws Exception {
    assertTrue(HANDLER_POSTFIX.isPreprocessingPhaseAllowed());
    assertFalse(HANDLER_POSTFIX.isGlobalPhaseAllowed());
    assertTrue(HANDLER_PREFIX.isPreprocessingPhaseAllowed());
    assertFalse(HANDLER_PREFIX.isGlobalPhaseAllowed());
  }

  @Override
  public void testArgumentType() throws Exception {
    assertEquals(DirectiveArgumentType.ONOFF, HANDLER_POSTFIX.getArgumentType());
    assertEquals(DirectiveArgumentType.ONOFF, HANDLER_PREFIX.getArgumentType());
  }
}
