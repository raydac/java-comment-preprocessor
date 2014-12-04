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

public class IfElseEndifDirectiveWithKeepLinesHandlerTest extends IfElseEndifDirectiveHandlerTest {

  private static final IfDirectiveHandler IF_HANDLER = new IfDirectiveHandler();
  private static final ElseDirectiveHandler ELSE_HANDLER = new ElseDirectiveHandler();
  private static final EndIfDirectiveHandler ENDIF_HANDLER = new EndIfDirectiveHandler();

  @Override
  public void testExecution() throws Exception {
    assertFilePreprocessing("directive_if_else_endif_with_keptlines.txt", true, null, null);
  }
}
