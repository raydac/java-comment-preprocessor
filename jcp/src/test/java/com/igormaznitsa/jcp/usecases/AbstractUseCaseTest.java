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

import com.igormaznitsa.jcp.JcpPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.Assert.*;

public abstract class AbstractUseCaseTest {

  protected TemporaryFolder tmpResultFolder;
  protected File sourceFolder;
  protected File etalonFolder;

  @Before
  public void before() throws Exception {

    final File testDir = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

    final File base = new File(testDir, this.getClass().getName().replace('.', File.separatorChar));

    final File simulfolder = new File(testDir.getParentFile(), "usecase_tests");
    if (!simulfolder.isDirectory()) {
      assertTrue("Can't make folders for simulation", simulfolder.mkdirs());
    }

    tmpResultFolder = new TemporaryFolder(simulfolder);
    tmpResultFolder.create();

    sourceFolder = new File(base, "src");
    etalonFolder = new File(base, "etl");
  }

  @After
  public void after() throws Exception {
    if (deleteResult()) {
      try {
        FileUtils.cleanDirectory(tmpResultFolder.getRoot());
      } finally {
        tmpResultFolder.delete();
      }
    }
  }

  public boolean deleteResult() {
    return true;
  }

  public abstract void check(PreprocessorContext context, JcpPreprocessor.Statistics stat) throws Exception;

  private void assertFolder(final File folder1, final File folder2, final boolean ignoreEOL) throws Exception {
    assertTrue("Folder 1 must be folder", folder1.isDirectory());
    assertTrue("Folder 2 must be folder", folder2.isDirectory());

    final File[] folder1files = folder1.listFiles();
    File[] folde2files = folder2.listFiles();
    assertEquals("Must have the same number of files and folders", folder1files.length, folde2files.length);

    for (final File f : folder1files) {
      final File f2 = new File(folder2, f.getName());
      if (!f2.exists()) {
        fail("Doesn't exist :" + f2.getAbsolutePath());
      }
      if (f.isFile() && !f2.isFile()) {
        fail("Must be file : " + f2.getAbsolutePath());
      } else if (f.isDirectory()) {
        if (!f2.isDirectory()) {
          fail("Must be file : " + f2.getAbsolutePath());
        } else {
          assertFolder(f, f2, ignoreEOL);
        }
      } else {
        final boolean equalsLength = ignoreEOL ? true : f.length() == f2.length();
        if (!equalsLength) {
          String fileOne = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
          String fileTwo = FileUtils.readFileToString(f2, StandardCharsets.UTF_8);

          System.err.println("FILE ONE=====================");
          System.err.println(fileOne);
          System.err.println("=============================");

          System.err.println("FILE TWO=====================");
          System.err.println(fileTwo);
          System.err.println("=============================");

          if (ignoreEOL) {
            assertEquals("File content must be same", fileOne.replace('\r', ' ').replace('\n', ' '), fileTwo.replace('\r', ' ').replace('\n', ' '));
          } else {
            assertEquals("File content must be same", fileOne, fileTwo);
          }
        }
        if (!ignoreEOL) {
          assertEquals("Checksum must be equal (" + f.getName() + ')', FileUtils.checksumCRC32(f), FileUtils.checksumCRC32(f2));
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

  protected boolean isIgnoreEolInCheck() {
    return true;
  }

  @Test
  public final void main() throws Exception {
    final PreprocessorContext context = new PreprocessorContext(new File("some_impossible_folder_121212"));
    context.setClearTarget(true);
    context.setSources(Collections.singletonList(this.sourceFolder.getAbsolutePath()));
    context.setTarget(tmpResultFolder.getRoot());
    context.setExcludeExtensions(Collections.singletonList("xml"));
    context.setVerbose(true);

    tuneContext(context);

    System.setProperty("jcp.line.separator", "\n");

    JcpPreprocessor preprocessor = new JcpPreprocessor(context);
    final JcpPreprocessor.Statistics stat = preprocessor.execute();

    assertFolder(etalonFolder, tmpResultFolder.getRoot(), this.isIgnoreEolInCheck());

    check(context, stat);
  }
}
