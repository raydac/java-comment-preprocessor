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

import static org.junit.Assert.*;

public class IfDefDirectiveHandlerTest extends AbstractDirectiveHandlerAcceptanceTest {

  private static final IfDefDirectiveHandler HANDLER = new IfDefDirectiveHandler();

  @Override
  public void testExecution() throws Exception {
    assertFilePreprocessing("directive_ifdef.txt", false, null, null, new VariablePair("BYTECODE", "true"));

    try {
      assertFilePreprocessing("directive_ifdef.txt", false, null, null);
    }
    catch (LinesNotMatchException expected) {
      assertEquals("somebytecode", expected.getEtalonString());
      assertEquals("end", expected.getResultString());
    }
  }

  @Override
  public void testKeyword() throws Exception {
    assertEquals("ifdef", HANDLER.getName());
  }

  @Override
  public void testExecutionCondition() throws Exception {
    assertFalse(HANDLER.executeOnlyWhenExecutionAllowed());
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
    assertEquals(DirectiveArgumentType.VARNAME, HANDLER.getArgumentType());
  }
}
