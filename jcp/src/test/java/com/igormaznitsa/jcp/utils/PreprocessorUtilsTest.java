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

package com.igormaznitsa.jcp.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import org.junit.Test;

public class PreprocessorUtilsTest {

  @Test
  public void testReplaceStringPrefix() throws Exception {
    final String[] testData = new String[] {"--ae:123", "-homa-", "-hbd", "---Some", "-", "--"};
    final String[] processed =
        PreprocessorUtils.replaceStringPrefix(new String[] {"-", "--"}, "/", testData);
    assertArrayEquals(new String[] {"/ae:123", "/homa-", "/hbd", "/-Some", "/", "/"}, processed);
  }

  @Test
  public void testGenerateStringForChar() throws Exception {
    assertEquals("", PreprocessorUtils.generateStringForChar(' ', -1));
    assertEquals("", PreprocessorUtils.generateStringForChar(' ', 0));
    assertEquals(" ", PreprocessorUtils.generateStringForChar(' ', 1));
    assertEquals("aaa", PreprocessorUtils.generateStringForChar('a', 3));
  }

  @Test
  public void testReplacePartByChar() throws Exception {
    assertEquals("", PreprocessorUtils.replacePartByChar("", ' ', 33, 44));
    assertEquals("a  de", PreprocessorUtils.replacePartByChar("abcde", ' ', 1, 2));
    assertEquals("a    ", PreprocessorUtils.replacePartByChar("abcde", ' ', 1, 8));
    assertEquals("abc  ", PreprocessorUtils.replacePartByChar("abcde", ' ', 3, 8));
  }

  @Test
  public void testMakeFileReader_charsetAndBufferSizeChange() throws Exception {
    final Charset defaultCharset = Charset.defaultCharset();
    final File testFile = new File(PreprocessorUtilsTest.class.getResource("somefile.txt").toURI());

    Charset nonDefaultCharset = null;
    for (final Charset ch : Charset.availableCharsets().values()) {
      if (!defaultCharset.equals(ch)) {
        nonDefaultCharset = ch;
        break;
      }
    }

    assertNotNull(
        "We must have found a non default charset, system must have more than one available charset",
        nonDefaultCharset);

    // some hack to get access to the wrapped reader
    final Field inField = BufferedReader.class.getDeclaredField("in");
    inField.setAccessible(true);
    final Field cbField = BufferedReader.class.getDeclaredField("cb");
    cbField.setAccessible(true);

    final int BUFFER_SIZE = 0xCAFE;

    final BufferedReader reader =
        PreprocessorUtils.makeFileReader(testFile, nonDefaultCharset, BUFFER_SIZE);

    // check that we have selected the non standard charset
    final InputStreamReader wrappedReader = (InputStreamReader) inField.get(reader);
    assertNotNull("Must not be null", wrappedReader);
    assertEquals("The non default charset must be set", nonDefaultCharset.name(),
        wrappedReader.getEncoding());

    // check that we have changed the buffer size
    final char[] insideCharBuffer = (char[]) cbField.get(reader);
    assertNotNull("Must not be null", insideCharBuffer);
    assertEquals("Must have our selected size", BUFFER_SIZE, insideCharBuffer.length);

  }
}
