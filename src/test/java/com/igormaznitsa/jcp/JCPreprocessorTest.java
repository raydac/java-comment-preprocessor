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
package com.igormaznitsa.jcp;

import com.igormaznitsa.jcp.cmdline.CommandLineHandler;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import java.io.*;
import java.util.*;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Test;

public final class JCPreprocessorTest {

  private void assertGVDFPreprocessorException(final String file, final int stringIndex) throws Exception {
    final PreprocessorContext context = new PreprocessorContext();
    context.addConfigFile(new File(this.getClass().getResource(file).toURI()));
    final JCPreprocessor preprocessor = new JCPreprocessor(context);
    try {
      preprocessor.processCfgFiles();
      fail("Must throw a PreprocessorException");
    }
    catch (PreprocessorException expected) {
      if (stringIndex != expected.getStringIndex()) {
        fail("Wrong error string index [" + expected.toString() + ']');
      }
    }
  }

  @Test
  public void testProcessGlobalVarDefiningFiles() throws Exception {
    final PreprocessorContext context = new PreprocessorContext();
    context.addConfigFile(new File(this.getClass().getResource("./global_ok.txt").toURI()));
    final JCPreprocessor preprocessor = new JCPreprocessor(context);
    preprocessor.processCfgFiles();

    assertEquals("Must have the variable", "hello world", context.findVariableForName("globalVar1").asString());
    assertEquals("Must have the variable", Value.INT_THREE, context.findVariableForName("globalVar2"));
    assertEquals("Character input encoding must be changed", "ISO-8859-1", context.getInCharacterEncoding());
  }

  @Test
  public void testProcessGlobalVarDefiningFiles_ATsymbol() throws Exception {
    assertGVDFPreprocessorException("global_error_at.txt", 8);
  }

  @Test
  public void testJavaCommentRemoving() throws Exception {

    final File testDirectory = new File(getClass().getResource("removers/java").toURI());
    final File resultFile = new File(testDirectory, "w_o_comments.ttt");
    final File etalonFile = new File(testDirectory, "etalon.etl");

    if (resultFile.exists()) {
      assertTrue("We have to remove the existing result file", resultFile.delete());
    }

    final PreprocessorContext context = new PreprocessorContext();
    context.setSourceDirectories(testDirectory.getCanonicalPath());
    context.setDestinationDirectory(testDirectory.getCanonicalPath());
    context.setClearDestinationDirBefore(false);
    context.setRemoveComments(true);
    context.setProcessingFileExtensions("ppp");
    context.setExcludedFileExtensions("etl");

    final JCPreprocessor preprocessor = new JCPreprocessor(context);
    preprocessor.execute();

    assertTrue("There must be the result file", resultFile.exists());
    assertTrue("There must be the etalon file", etalonFile.exists());

    String differentLine = null;
    int lineIndex = 1;

    BufferedReader resultReader = null;
    BufferedReader etalonReader = null;
    try {
      resultReader = new BufferedReader(new FileReader(resultFile));
      etalonReader = new BufferedReader(new FileReader(etalonFile));

      while (true) {
        final String resultStr = resultReader.readLine();
        final String etalonStr = etalonReader.readLine();
        if (resultStr == null && etalonStr == null) {
          break;
        }

        if (resultStr == null || !resultStr.equals(etalonStr)) {
          differentLine = resultStr;
          break;
        }

        lineIndex++;
      }

    }
    finally {
      IOUtils.closeQuietly(etalonReader);
      IOUtils.closeQuietly(resultReader);
    }

    if (differentLine != null) {
      fail("Line " + lineIndex + " There is a different strings [" + differentLine + '[');
    }
  }

  @Test
  public void testCLIHandlerNameConflicts(){
    final List<String> checked = new ArrayList<String>();
    for(final CommandLineHandler h : JCPreprocessor.COMMAND_LINE_HANDLERS){
      final String name = h.getKeyName();
      for(final String l : checked){
        if (l.startsWith(name) || name.startsWith(l)) fail("Conflict ["+l+" and "+name+']');
      }
      checked.add(name);
    }
  }
}
