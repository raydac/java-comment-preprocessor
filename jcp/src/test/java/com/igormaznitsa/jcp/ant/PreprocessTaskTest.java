/*
 * Copyright 2002-2019 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.igormaznitsa.jcp.ant;

import com.igormaznitsa.jcp.TestUtils;
import com.igormaznitsa.jcp.ant.PreprocessTask.Global;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import org.apache.tools.ant.Project;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PreprocessTaskTest {

  final static Project projectMock = mock(Project.class);
  static File THIS_DIRECTORY;

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
    final List<PreprocessorContext.SourceFolder> sourceDirs = antTask.generatePreprocessorContext().getSourceFolders();
    assertEquals("There must be only root", 1, sourceDirs.size());

    assertEquals("File must be equal the original", THIS_DIRECTORY, sourceDirs.get(0).getAsFile());
  }

  @Test
  public void testCopyFileAttributes() throws Exception {
    assertFalse(antTask.generatePreprocessorContext().isCopyFileAttributes());
    antTask.setCopyFileAttributes(true);
    assertTrue(antTask.generatePreprocessorContext().isCopyFileAttributes());
  }

  @Test
  public void testExcludedFolders() throws Exception {
    assertTrue(antTask.generatePreprocessorContext().getExcludedFolderPatterns().isEmpty());
    antTask.setExcludedFolders(".git" + File.pathSeparator + "**/.cvs" + File.pathSeparator + ".hg");
    assertEquals(Arrays.asList(".git", "**/.cvs", ".hg"), antTask.generatePreprocessorContext().getExcludedFolderPatterns());
  }

  @Test
  public void testCareForLastNextLine() throws Exception {
    antTask.setCareForLastNextLine(true);
    assertTrue("Must be true", antTask.generatePreprocessorContext().isCareForLastNextLine());
    antTask.setCareForLastNextLine(false);
    assertFalse("Must be false", antTask.generatePreprocessorContext().isCareForLastNextLine());
  }

  @Test
  public void testCompareDestination() throws Exception {
    antTask.setCompareDestiation(true);
    assertTrue("Must be true", antTask.generatePreprocessorContext().isCompareDestination());
    antTask.setCompareDestiation(false);
    assertFalse("Must be false", antTask.generatePreprocessorContext().isCompareDestination());
  }

  @Test
  public void testPreserveIndent() throws Exception {
    antTask.setPreserveIndent(true);
    assertTrue("Must be true", antTask.generatePreprocessorContext().isPreserveIndent());
    antTask.setPreserveIndent(false);
    assertFalse("Must be false", antTask.generatePreprocessorContext().isPreserveIndent());
  }

  @Test
  public void testSetDestination() throws Exception {
    antTask.setDestination(THIS_DIRECTORY);
    TestUtils.assertFilePath("Files must be equal", THIS_DIRECTORY.getCanonicalFile(), antTask.generatePreprocessorContext().getDestinationDirectoryAsFile());
  }

  @Test
  public void testSetInCharset() throws Exception {
    antTask.setInCharset(StandardCharsets.UTF_16LE.name());
    assertEquals("Must be the same charset", StandardCharsets.UTF_16LE, antTask.generatePreprocessorContext().getInCharset());
  }

  @Test
  public void testSetOutCharset() throws Exception {
    antTask.setOutCharset(StandardCharsets.UTF_16BE.name());
    assertEquals("Must be the same charset", StandardCharsets.UTF_16BE, antTask.generatePreprocessorContext().getOutCharset());
  }

  @Test
  public void testSetUnknownAsFalse() throws Exception {
    antTask.setUnknownVarAsFalse(true);
    assertTrue(antTask.generatePreprocessorContext().isUnknownVariableAsFalse());
  }

  @Test
  public void testSetExcluded() throws Exception {
    final String TEST = "bin,vb,cpp";
    antTask.setExcluded(TEST);
    final String[] splitted = TEST.split(",");
    final String[] contextExtensions = antTask.generatePreprocessorContext().getExcludedFileExtensions().toArray(new String[0]);
    final Set<String> thoseExts = new HashSet<>(Arrays.asList(contextExtensions));
    assertEquals("Must have the same size", splitted.length, thoseExts.size());
    assertTrue("Must contains all extensions", new HashSet<>(Arrays.asList(splitted)).containsAll(thoseExts));
  }

  @Test
  public void testSetExtensions() throws Exception {
    final String TEST = "pl,frt,bat";
    antTask.setProcessing(TEST);
    final String[] splitted = TEST.split(",");
    final String[] contextExtensions = antTask.generatePreprocessorContext().getProcessingFileExtensions();
    final Set<String> thoseExts = new HashSet<>(Arrays.asList(contextExtensions));
    assertEquals("Must have the same size", splitted.length, thoseExts.size());
    assertTrue("Must contains all extensions", new HashSet<>(Arrays.asList(splitted)).containsAll(thoseExts));
  }

  @Test
  public void testSetClear() throws Exception {
    antTask.setClear(true);
    assertTrue("Must be true", antTask.generatePreprocessorContext().doesClearDestinationDirBefore());
    antTask.setClear(false);
    assertFalse("Must be false", antTask.generatePreprocessorContext().doesClearDestinationDirBefore());
  }

  @Test
  public void testSetAllowSpaceBeforeDirectives() throws Exception {
    antTask.setAllowWhitespace(false);
    assertFalse("Must be false", antTask.generatePreprocessorContext().isAllowWhitespace());
    antTask.setAllowWhitespace(true);
    assertTrue("Must be true", antTask.generatePreprocessorContext().isAllowWhitespace());
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

    final Value value = antTask.generatePreprocessorContext().findVariableForName("hello_world", true);
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
    assertEquals("Must be equal", file1.getCanonicalFile(), cfgFiles[0].getCanonicalFile());
    assertEquals("Must be equal", file2.getCanonicalFile(), cfgFiles[1].getCanonicalFile());
  }
}
