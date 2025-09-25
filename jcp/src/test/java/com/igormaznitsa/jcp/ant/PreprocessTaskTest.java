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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.igormaznitsa.jcp.context.CommentRemoverType;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import org.apache.tools.ant.Project;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
    THIS_DIRECTORY =
        new File(com.igormaznitsa.jcp.ant.PreprocessTaskTest.class.getResource("./").toURI());
  }

  @Before
  public void beforeTest() {
    antTask = new PreprocessTask();
    antTask.setProject(projectMock);

    final PreprocessTask.Sources sources = new PreprocessTask.Sources();
    sources.createPath().setValue(THIS_DIRECTORY.getAbsolutePath());

    antTask.setSources(sources);
  }

  @Test
  public void testSetSources() {
    final List<PreprocessorContext.SourceFolder> sourceDirs =
        antTask.makePreprocessorContext().getSources();
    assertEquals(1, sourceDirs.size());
    assertEquals(THIS_DIRECTORY, sourceDirs.get(0).getAsFile());
  }

  @Test
  public void testTarget() {
    antTask.setTarget(THIS_DIRECTORY.getAbsolutePath());
    assertEquals(THIS_DIRECTORY, antTask.makePreprocessorContext().getTarget());
  }

  @Test
  public void testSourceEncoding() {
    final String TEST = "ISO-8859-1";
    antTask.setSourceEncoding(TEST);
    assertEquals(StandardCharsets.ISO_8859_1,
        antTask.makePreprocessorContext().getSourceEncoding());
  }

  @Test
  public void testTargetEncoding() {
    final String TEST = "ISO-8859-1";
    antTask.setTargetEncoding(TEST);
    assertEquals(StandardCharsets.ISO_8859_1,
        antTask.makePreprocessorContext().getTargetEncoding());
  }

  @Test
  public void testExcludedExtensions() {
    final PreprocessTask.ExcludeExtensions antextensions = antTask.createExcludeExtensions();
    antextensions.createExtension().addText("bin");
    antextensions.createExtension().addText("vb");
    antextensions.createExtension().addText("cpp");
    final Set<String> extensions = antTask.makePreprocessorContext().getExcludeExtensions();
    assertEquals(3, extensions.size());
    assertTrue(extensions.contains("bin"));
    assertTrue(extensions.contains("vb"));
    assertTrue(extensions.contains("cpp"));
  }

  @Test
  public void testExtensions() {
    final PreprocessTask.Extensions antextensions = antTask.createExtensions();
    antextensions.createExtension().addText("bin");
    antextensions.createExtension().addText("vb");
    antextensions.createExtension().addText("cpp");
    final Set<String> extensions = antTask.makePreprocessorContext().getExtensions();
    assertEquals(3, extensions.size());
    assertTrue(extensions.contains("bin"));
    assertTrue(extensions.contains("vb"));
    assertTrue(extensions.contains("cpp"));
  }


  @Test
  public void testClearTarget() {
    antTask.setClearTarget(true);
    assertTrue(antTask.makePreprocessorContext().isClearTarget());
    antTask.setClearTarget(false);
    assertFalse(antTask.makePreprocessorContext().isClearTarget());
  }

  @Test
  public void testKeepComments() throws Exception {
    antTask.setKeepComments("true");
    assertEquals(CommentRemoverType.KEEP_ALL, antTask.makePreprocessorContext().getKeepComments());
    antTask.setKeepComments("false");
    assertEquals(CommentRemoverType.REMOVE_C_STYLE,
        antTask.makePreprocessorContext().getKeepComments());
  }

  @Test
  public void testVerbose() throws Exception {
    antTask.setVerbose(true);
    assertTrue(antTask.makePreprocessorContext().isVerbose());
    antTask.setVerbose(false);
    assertFalse(antTask.makePreprocessorContext().isVerbose());
  }

  @Test
  public void testAllowWhitespace() {
    antTask.setAllowWhitespaces(true);
    assertTrue(antTask.makePreprocessorContext().isAllowWhitespaces());
    antTask.setAllowWhitespaces(false);
    assertFalse(antTask.makePreprocessorContext().isVerbose());
  }

  @Test
  public void testCareForLastEol() {
    antTask.setCareForLastEol(true);
    assertTrue(antTask.makePreprocessorContext().isCareForLastEol());
    antTask.setCareForLastEol(false);
    assertFalse(antTask.makePreprocessorContext().isCareForLastEol());
  }

  @Test
  public void testEol() {
    antTask.setEol("someeol\\r\\n");
    assertEquals("someeol\r\n", antTask.makePreprocessorContext().getEol());
  }

  @Test
  public void testPreserveIndents() {
    antTask.setPreserveIndents(true);
    assertTrue(antTask.makePreprocessorContext().isPreserveIndents());
    antTask.setPreserveIndents(false);
    assertFalse(antTask.makePreprocessorContext().isPreserveIndents());
  }

  @Test
  public void testDontOverwriteSameContent() {
    antTask.setDontOverwriteSameContent(true);
    assertTrue(antTask.makePreprocessorContext().isDontOverwriteSameContent());
    antTask.setDontOverwriteSameContent(false);
    assertFalse(antTask.makePreprocessorContext().isDontOverwriteSameContent());
  }

  @Test
  public void testDryRun() {
    antTask.setDryRun(true);
    assertTrue(antTask.makePreprocessorContext().isDryRun());
    antTask.setDryRun(false);
    assertFalse(antTask.makePreprocessorContext().isDryRun());
  }

  @Test
  public void testUnknownVarAsFalse() {
    antTask.setUnknownVarAsFalse(true);
    assertTrue(antTask.makePreprocessorContext().isUnknownVariableAsFalse());
    antTask.setUnknownVarAsFalse(false);
    assertFalse(antTask.makePreprocessorContext().isUnknownVariableAsFalse());
  }

  @Test
  public void testAddGlobal() {
    final PreprocessTask.Vars vars = antTask.createVars();
    final PreprocessTask.Vars.Var var = vars.createVar();
    var.setName("hello_world");
    var.setValue("4");
    assertEquals(Value.INT_FOUR,
        antTask.makePreprocessorContext().findVariableForName("hello_world", false));
  }

  @Test
  public void testAddCfgFile() {
    final PreprocessTask.ConfigFiles configFiles = antTask.createConfigFiles();
    configFiles.createPath().setValue("what/that");
    configFiles.createPath().setValue("what/those");

    final List<File> foundConfigFiles = antTask.makePreprocessorContext().getConfigFiles();
    assertEquals("Must be 2", 2, foundConfigFiles.size());
    assertEquals("base/dir/what/that".replace('/', File.separatorChar),
        foundConfigFiles.get(0).getPath());
    assertEquals("base/dir/what/those".replace('/', File.separatorChar),
        foundConfigFiles.get(1).getPath());
  }
}
