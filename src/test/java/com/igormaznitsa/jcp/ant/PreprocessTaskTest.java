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
package com.igormaznitsa.jcp.ant;

import com.igormaznitsa.jcp.ant.PreprocessTask.Global;
import com.igormaznitsa.jcp.expression.Value;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import org.apache.tools.ant.Project;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class PreprocessTaskTest {

  static File THIS_DIRECTORY;

  final static Project projectMock = mock(Project.class);

  static {
    when(projectMock.getBaseDir()).thenReturn(new File("base/dir"));
    when(projectMock.getProperties()).thenReturn(new Hashtable<String, Object>());
  }

  PreprocessTask antTask;

  @BeforeClass
  public static void beforeClass() throws Exception {
    THIS_DIRECTORY = new File(PreprocessTaskTest.class.getResource("./").toURI());
  }

  @Before
  public void beforeTest() {
    antTask = new PreprocessTask();
    antTask.setProject(projectMock);
    antTask.setSource(THIS_DIRECTORY);
  }

  @Test
  public void testSetSource() throws Exception {
    final File[] sourceDirs = antTask.generatePreprocessorContext().getSourceDirectoryAsFiles();
    assertEquals("There must be only root", 1, sourceDirs.length);

    assertEquals("File must be equal the original", THIS_DIRECTORY, sourceDirs[0]);
  }

  @Test
  public void testCareForLastNextLine() throws Exception {
    antTask.setCareForLastNextLine(true);
    assertTrue("Must be true", antTask.generatePreprocessorContext().isCareForLastNextLine());
    antTask.setCareForLastNextLine(false);
    assertFalse("Must be false", antTask.generatePreprocessorContext().isCareForLastNextLine());
  }

  @Test
  public void testSetDestination() throws Exception {
    antTask.setDestination(THIS_DIRECTORY);
    assertEquals("Files must be equal", THIS_DIRECTORY.getCanonicalFile(), antTask.generatePreprocessorContext().getDestinationDirectoryAsFile());
  }

  @Test
  public void testSetInCharset() throws Exception {
    final String TEST = "ISO-8859-1";
    antTask.setInCharset(TEST);
    assertEquals("Must be the same charset", TEST, antTask.generatePreprocessorContext().getInCharacterEncoding());
  }

  @Test
  public void testSetOutCharset() throws Exception {
    final String TEST = "ISO-8859-1";
    antTask.setOutCharset(TEST);
    assertEquals("Must be the same charset", TEST, antTask.generatePreprocessorContext().getOutCharacterEncoding());
  }

  @Test
  public void testSetExcluded() throws Exception {
    final String TEST = "bin,vb,cpp";
    antTask.setExcluded(TEST);
    final String[] splitted = TEST.split(",");
    final String[] contextExtensions = antTask.generatePreprocessorContext().getExcludedFileExtensions();
    final Set<String> thoseExts = new HashSet<String>(Arrays.asList(contextExtensions));
    assertEquals("Must have the same size", splitted.length, thoseExts.size());
    assertTrue("Must contains all extensions", new HashSet<String>(Arrays.asList(splitted)).containsAll(thoseExts));
  }

  @Test
  public void testSetExtensions() throws Exception {
    final String TEST = "pl,frt,bat";
    antTask.setProcessing(TEST);
    final String[] splitted = TEST.split(",");
    final String[] contextExtensions = antTask.generatePreprocessorContext().getProcessingFileExtensions();
    final Set<String> thoseExts = new HashSet<String>(Arrays.asList(contextExtensions));
    assertEquals("Must have the same size", splitted.length, thoseExts.size());
    assertTrue("Must contains all extensions", new HashSet<String>(Arrays.asList(splitted)).containsAll(thoseExts));
  }

  @Test
  public void testSetClear() throws Exception {
    antTask.setClear(true);
    assertTrue("Must be true", antTask.generatePreprocessorContext().doesClearDestinationDirBefore());
    antTask.setClear(false);
    assertFalse("Must be false", antTask.generatePreprocessorContext().doesClearDestinationDirBefore());
  }

  @Test
  public void testSetRemoveComments() throws Exception {
    antTask.setRemoveComments(true);
    assertTrue("Must be true", antTask.generatePreprocessorContext().isRemoveComments());
    antTask.setRemoveComments(false);
    assertFalse("Must be false", antTask.generatePreprocessorContext().isRemoveComments());
  }

  @Test
  public void testSetVerbose() throws Exception {
    antTask.setVerbose(true);
    assertTrue("Must be true", antTask.generatePreprocessorContext().isVerbose());
    antTask.setVerbose(false);
    assertFalse("Must be false", antTask.generatePreprocessorContext().isVerbose());
  }

  @Test
  public void testSetDisableOut() throws Exception {
    antTask.setDisableOut(true);
    assertTrue("Must be true", antTask.generatePreprocessorContext().isFileOutputDisabled());
    antTask.setDisableOut(false);
    assertFalse("Must be false", antTask.generatePreprocessorContext().isFileOutputDisabled());
  }

  @Test
  public void testAddGlobal() throws Exception {
    final Global global = antTask.createGlobal();
    global.setName("hello_world");
    global.setValue("4");

    final Value value = antTask.generatePreprocessorContext().findVariableForName("hello_world");
    assertEquals("Must be 4", Value.INT_FOUR, value);
  }

  @Test
  public void testAddCfgFile() throws Exception {
    final File file1 = new File("what/that");
    final File file2 = new File("what/those");

    final PreprocessTask.CfgFile cfgFile1 = antTask.createCfgFile();
    cfgFile1.setFile(file1);
    final PreprocessTask.CfgFile cfgFile2 = antTask.createCfgFile();
    cfgFile2.setFile(file2);

    final File[] cfgFiles = antTask.generatePreprocessorContext().getConfigFiles();
    assertEquals("Must be 2", 2, cfgFiles.length);
    assertEquals("Must be equals", file1.getCanonicalFile(), cfgFiles[0].getCanonicalFile());
    assertEquals("Must be equals", file2.getCanonicalFile(), cfgFiles[1].getCanonicalFile());
  }
}
