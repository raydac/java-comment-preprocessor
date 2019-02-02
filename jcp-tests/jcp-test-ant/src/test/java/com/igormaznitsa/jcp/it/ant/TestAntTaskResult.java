/*
 * Copyright 2019 Igor Maznitsa.
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

package com.igormaznitsa.jcp.it.ant;

import org.apache.tools.ant.util.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAntTaskResult {

  private final File RESULT_FOLDER = new File(System.getProperty("jcp.target.folder"));

  @Test
  public void testPreprocessResult() throws Exception {
    assertTrue(RESULT_FOLDER.isDirectory(), "Target folder must be created");
    final File folder = new File(RESULT_FOLDER, "com/igormaznitsa/dummyproject");
    final File originalFile = new File(folder, "main.java");
    final File resultFile = new File(folder, "testmain2.java");

    assertFalse(originalFile.isFile(), "original file must not be presented");
    assertTrue(resultFile.isFile(), "preprocessed file must be presented");
    String body;
    try (Reader reader = new FileReader(resultFile)) {
      body = FileUtils.readFully(reader);
    }
    assertFalse(body.contains("//#"));
    assertFalse(body.contains("/*"));
    assertTrue(body.contains("hellocfg"));
  }
}
