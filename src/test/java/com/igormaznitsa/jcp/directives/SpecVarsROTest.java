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

import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import org.mockito.Mockito;

/**
 * Test for special read only predefined variables
 */
public class SpecVarsROTest extends AbstractDirectiveHandlerAcceptanceTest {

  @Override
  public void testExecution() throws Exception {
    final PreprocessorLogger mock = Mockito.mock(PreprocessorLogger.class);
    assertFilePreprocessing("specvars_ro.txt", false, null, mock);
  }

  @Override
  public void testKeyword() throws Exception {
  }

  @Override
  public void testExecutionCondition() throws Exception {
  }

  @Override
  public void testReference() throws Exception {
  }

  @Override
  public void testArgumentType() throws Exception {
  }

  @Override
  public void testPhase() throws Exception {
  }
}
