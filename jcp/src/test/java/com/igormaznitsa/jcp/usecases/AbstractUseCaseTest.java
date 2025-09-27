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

package com.igormaznitsa.jcp.usecases;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.igormaznitsa.jcp.JcpPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public abstract class AbstractUseCaseTest {

  protected TemporaryFolder tmpResultFolder;
  protected File sourceFolder;
  protected File etalonFolder;

  @Before
  public void before() throws Exception {

    final File testDir =
        new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

    final File base = new File(testDir, this.getClass().getName().replace('.', File.separatorChar));

    final File simulationFolder = new File(testDir.getParentFile(), "usecase_tests");
    if (!simulationFolder.isDirectory()) {
      assertTrue("Can't make folders for simulation", simulationFolder.mkdirs());
    }

    this.tmpResultFolder = new TemporaryFolder(simulationFolder);
    this.tmpResultFolder.create();

    this.sourceFolder = new File(base, "src");
    this.etalonFolder = new File(base, "etl");
  }

  @After
  public void after() throws Exception {
    if (this.isDeleteTemporaryFolder()) {
      try {
        FileUtils.cleanDirectory(tmpResultFolder.getRoot());
      } finally {
        this.tmpResultFolder.delete();
      }
    }
  }

  public boolean isDeleteTemporaryFolder() {
    return true;
  }

  public abstract void check(PreprocessorContext context, JcpPreprocessor.Statistics stat)
      throws Exception;

  private void assertFolder(final File etalonFolder, final File checkFolder,
                            final boolean ignoreEOL)
      throws Exception {
    assertTrue("Etalon folder must be a folder", etalonFolder.isDirectory());
    assertTrue("Checked folder must be folder", checkFolder.isDirectory());

    final File[] etalonFolderFiles = requireNonNull(etalonFolder.listFiles());
    final File[] checkFolderFiles = requireNonNull(checkFolder.listFiles());
    assertEquals("Must have the same number of files and folders", etalonFolderFiles.length,
        checkFolderFiles.length);

    for (final File etalonFile : etalonFolderFiles) {
      final File checkFile = new File(checkFolder, etalonFile.getName());
      if (!checkFile.exists()) {
        fail("Can't find generated file :" + checkFile.getAbsolutePath());
      }
      if (etalonFile.isFile() && !checkFile.isFile()) {
        fail("Expected file: " + checkFile.getAbsolutePath());
      } else if (etalonFile.isDirectory()) {
        if (!checkFile.isDirectory()) {
          fail("Expected folder: " + checkFile.getAbsolutePath());
        } else {
          assertFolder(etalonFile, checkFile, ignoreEOL);
        }
      } else {
        if (ignoreEOL) {
          final String[] etalonLines =
              FileUtils.readFileToString(etalonFile, StandardCharsets.UTF_8).split("\\R", -1);
          final String[] checkLines =
              FileUtils.readFileToString(checkFile, StandardCharsets.UTF_8).split("\\R", -1);

          if (etalonLines.length != checkLines.length) {
            System.err.println(
                "----Etalon----\n" + String.join(System.lineSeparator(), etalonLines));
            System.err.println(
                "----Checking----\n" + String.join(System.lineSeparator(), checkLines));
            fail("Different number of lines, expected " + etalonLines.length + " but read " +
                checkLines.length + " : " + checkFile.getAbsolutePath());
          }
          for (int j = 0; j < etalonLines.length; j++) {
            final String etalon = etalonLines[j];
            final String check = checkLines[j];
            if (!etalon.equals(check)) {
              fail("Difference at line " + (j + 1) + ": etalon='" + etalon + "', check='" + check +
                  '\'');
            }
          }
        } else {
          final long checksumEtalon = FileUtils.checksumCRC32(etalonFile);
          final long checksumTested = FileUtils.checksumCRC32(checkFile);
          if (checksumEtalon != checksumTested) {
            fail("Wrong checksum, etalon file = " + etalonFile.getAbsolutePath() +
                "  , check file " + checkFile.getAbsolutePath());
          }
        }
      }
    }
  }

  /**
   * Allows to tune preprocessor context.
   *
   * @param context preprocessor context
   */
  protected void tuneContext(final PreprocessorContext context) {

  }

  protected void tuneDefaultContextOptions(final PreprocessorContext context) {
    context.setClearTarget(true);
    context.setSources(Collections.singletonList(this.sourceFolder.getAbsolutePath()));
    context.setTarget(tmpResultFolder.getRoot());
    context.setExcludeExtensions(Collections.singletonList("xml"));
    context.setVerbose(true);
  }

  protected boolean isIgnoreEolInCheck() {
    return true;
  }

  protected PreprocessorContext createPreprocessorContext(final File baseFolder) {
    return new PreprocessorContext(baseFolder);
  }

  @Test
  public final void executeTest() throws Exception {
    final PreprocessorContext context =
        createPreprocessorContext(new File("some_impossible_folder_121212"));
    this.tuneDefaultContextOptions(context);
    this.tuneContext(context);
    System.setProperty("jcp.line.separator", "\n");

    final JcpPreprocessor preprocessor = new JcpPreprocessor(context);
    final JcpPreprocessor.Statistics preprocessorStatistics = preprocessor.execute();

    this.assertFolder(this.etalonFolder, this.tmpResultFolder.getRoot(), this.isIgnoreEolInCheck());
    this.check(context, preprocessorStatistics);
  }
}
