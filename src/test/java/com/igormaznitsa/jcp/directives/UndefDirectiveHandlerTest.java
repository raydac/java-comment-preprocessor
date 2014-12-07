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

import com.igormaznitsa.jcp.context.PreprocessorContext;
import org.junit.Test;
import static org.junit.Assert.*;

public class UndefDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {

  private static final UndefDirectiveHandler HANDLER = new UndefDirectiveHandler();

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext context = assertFilePreprocessing("directive_undef.txt", false, null, null, new VariablePair("somevar", "true"));
    assertFalse(context.isGlobalVariable("somevar"));
    assertFalse(context.isLocalVariable("localone"));
  }

  @Test
  public void testExecution_wrongCases() {
    assertPreprocessorException("\n\n//#undef \n", 3, null);
    assertPreprocessorException("\n\n//#undef 1223\n", 3, null);
    assertPreprocessorException("\n\n//#undef \"test\"\n", 3, null);
  }

  @Override
  public void testKeyword() throws Exception {
    assertEquals("undef", HANDLER.getName());
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
  public void testPhase() throws Exception {
    assertFalse(HANDLER.isGlobalPhaseAllowed());
    assertTrue(HANDLER.isPreprocessingPhaseAllowed());
  }

  @Override
  public void testArgumentType() throws Exception {
    assertEquals(DirectiveArgumentType.TAIL, HANDLER.getArgumentType());
  }

}
