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
package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.File;
import static org.junit.Assert.*;
import org.junit.Test;

public class GlobalVariableDefiningFileHandlerTest extends AbstractCommandLineHandlerTest {

  private static final GlobalVariableDefiningFileHandler HANDLER = new GlobalVariableDefiningFileHandler();

  @Override
  public void testThatTheHandlerInTheHandlerList() {
    assertHandlerInTheHandlerList(HANDLER);
  }

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext context = new PreprocessorContext();

    final File testFile = new File(this.getClass().getResource("./global_variable_def.txt").toURI());

    final String param = "@" + PreprocessorUtils.getFilePath(testFile);

    HANDLER.processCommandLineKey(param, context);

    final File[] configFiles = context.getConfigFiles();

    assertEquals("File must be added", 1, configFiles.length);
    assertEquals("File must be equals", testFile, configFiles[0]);
  }

  @Test
  public void testExecution_Expression() throws Exception {
    final PreprocessorContext context = new PreprocessorContext();

    final File testFile = new File(this.getClass().getResource("./global_variable_def.txt").toURI());

    final String path = testFile.getParent().replace('\'', '/').replace("\"", "\\\"");

    final String param = "@@\"" + path.replace("\\", "\\\\") + "\"+\"/\"+\"" + "global_variable_def.txt" + "\"";

    HANDLER.processCommandLineKey(param, context);

    final File[] globalVarFiles = context.getConfigFiles();

    assertEquals("File must be added", 1, globalVarFiles.length);
    assertEquals("File must be equals", testFile, globalVarFiles[0]);
  }

  @Test(expected = PreprocessorException.class)
  public void testExecution_nonExistingFileWithExpression() {
    final PreprocessorContext context = new PreprocessorContext();
    HANDLER.processCommandLineKey("@@\"undefinded_file.111111.txtt\"", context);
  }

  @Test(expected = PreprocessorException.class)
  public void testExecution_nonExistingFile() {
    final PreprocessorContext context = new PreprocessorContext();
    HANDLER.processCommandLineKey("@undefinded_file.111111.txtt", context);
  }

  @Test(expected = PreprocessorException.class)
  public void testExecution_emptyFile() {
    final PreprocessorContext context = new PreprocessorContext();
    HANDLER.processCommandLineKey("@", context);
  }

  @Test(expected = PreprocessorException.class)
  public void testExecution_emptyFileForExpressionMode() {
    final PreprocessorContext context = new PreprocessorContext();
    HANDLER.processCommandLineKey("@@", context);
  }

  @Override
  public void testName() {
    assertEquals("@", HANDLER.getKeyName());
  }

  @Override
  public void testDescription() {
    assertDescription(HANDLER);
  }
}
