/*
 * Copyright 2017 Igor Maznitsa.
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
package com.igormaznitsa.jcp.cmdline;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import java.io.File;
import com.igormaznitsa.jcp.context.PreprocessorContext;

public class ExcludeFoldersHandlerTest extends AbstractCommandLineHandlerTest {

  private static final ExcludeFoldersHandler HANDLER = new ExcludeFoldersHandler();

  @Override
  public void testThatTheHandlerInTheHandlerList() {
    assertHandlerInTheHandlerList(HANDLER);
  }

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext mock = preparePreprocessorContext();

    assertFalse(HANDLER.processCommandLineKey("", mock));
    assertFalse(HANDLER.processCommandLineKey("/ed:", mock));
    assertFalse(HANDLER.processCommandLineKey("/Ed:", mock));
    assertFalse(HANDLER.processCommandLineKey("/ED", mock));

    assertTrue(HANDLER.processCommandLineKey("/ed:testdir/**/hd"+File.pathSeparator+"zoom"+File.pathSeparator+"g?df", mock));
    verify(mock).setExcludedFolderPatterns(new String[]{"testdir/**/hd","zoom","g?df"});
  }

  @Override
  public void testName() {
    assertEquals("/ED:", HANDLER.getKeyName());
  }

  @Override
  public void testDescription() {
    assertDescription(HANDLER);
  }
}
