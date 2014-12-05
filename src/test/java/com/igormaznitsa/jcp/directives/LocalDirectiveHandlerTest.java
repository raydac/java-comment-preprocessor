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

public class LocalDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {

  private static final LocalDirectiveHandler HANDLER = new LocalDirectiveHandler();

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext context = assertFilePreprocessing("directive_local.txt", false, null, null);

    assertEquals(Long.valueOf(5), context.getLocalVariable("x").asLong());
    assertEquals(Long.valueOf(10), context.getLocalVariable("y").asLong());
    assertEquals(Long.valueOf(15), context.getLocalVariable("z").asLong());
    assertEquals("", context.getLocalVariable("l_stringgamesNumber").asString());
  }

  @Override
  public void testExecutionCondition() throws Exception {
    assertTrue(HANDLER.executeOnlyWhenExecutionAllowed());
  }

  @Test
  public void testExecution_ExceptionOnExpressionAbsence() {
    assertPreprocessorException("1\n2\n   //#local \n3 ", 3, null);
    assertPreprocessorException("1\n2\n   //#local\n3   ", 3, null);
  }

  @Test
  public void testExecution_ExceptionOnWrongExpression() {
    assertPreprocessorException("1\n2\n   //#local 3\n3  ", 3, null);
    assertPreprocessorException("1\n2\n   //#local a=\n3", 3, null);
  }

  @Override
  public void testKeyword() throws Exception {
    assertEquals("local", HANDLER.getName());
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
    assertEquals(DirectiveArgumentType.SET, HANDLER.getArgumentType());
  }
}
