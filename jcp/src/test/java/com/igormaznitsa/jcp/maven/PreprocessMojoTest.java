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

package com.igormaznitsa.jcp.maven;

import static org.junit.Assert.assertArrayEquals;

import com.igormaznitsa.jcp.context.CommentRemoverType;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public final class PreprocessMojoTest extends AbstractMojoTestCase {

  private static void assertArrayEqualsWithoutOrders(final Object[] array1, final Object[] array2) {
    final List<Object> list1 = new ArrayList<>(Arrays.asList(array1));
    final List<Object> list2 = new ArrayList<>(Arrays.asList(array2));

    while (!list1.isEmpty() && !list2.isEmpty()) {
      final Object list1obj = list1.get(0);
      for (int i = 0; i < list2.size(); i++) {
        if (list2.get(i).equals(list1obj)) {
          list2.remove(i);
          break;
        }
      }
      list1.remove(0);
    }

    assertTrue("Different values in arrays", list1.isEmpty() && list2.isEmpty());
  }

  @Before
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @After
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Test
  public void testConfiguration() throws Exception {
    final File testPom = new File(this.getClass().getResource("test.pom.xml").toURI());
    assertTrue("Must be existing", testPom.exists());
    final PreprocessMojo mojo = (PreprocessMojo) lookupMojo("preprocess", testPom);
    assertNotNull("Must not be null", mojo);

    assertFalse(mojo.isSkip());
    mojo.setSkip(true);
    assertTrue(mojo.isSkip());

    final PreprocessorContext context = mojo.makePreprocessorContext();

    final String[] sources = context.getSources()
        .stream()
        .map(PreprocessorContext.SourceFolder::getAsString)
        .toArray(String[]::new);

    assertArrayEqualsWithoutOrders(new String[] {"/", "/some", "/another/some"}, sources);
    assertEquals("destination_dir", context.getTarget().getName());
    assertArrayEqualsWithoutOrders(new String[] {"xml", "html"}, context.getExcludeExtensions().toArray());
    assertArrayEqualsWithoutOrders(new String[] {"java", "txt"}, context.getExtensions().toArray());
    assertEquals(StandardCharsets.UTF_16, context.getSourceEncoding());
    assertEquals(StandardCharsets.US_ASCII, context.getTargetEncoding());
    assertEquals(CommentRemoverType.KEEP_ALL, context.getKeepComments());
    assertTrue(context.isVerbose());
    assertTrue(context.isDryRun());
    assertTrue(context.isClearTarget());
    assertTrue(context.isKeepLines());
    assertTrue(context.isCareForLastEol());
    assertTrue(context.isDontOverwriteSameContent());
    assertTrue(context.isAllowWhitespaces());
    assertTrue(context.isPreserveIndents());
    assertTrue(context.isKeepAttributes());
    assertTrue(context.isUnknownVariableAsFalse());

    assertArrayEquals(Arrays.asList(".git", ".hg", "**/.cvs", "c:\\hello\\**\\world").toArray(new String[0]), context.getExcludeFolders().toArray(new String[0]));

    final List<File> configFiles = context.getConfigFiles();
    assertEquals("Must be two", 2, configFiles.size());
    assertEquals("Must be test1.cfg", "test1.cfg", configFiles.get(0).getName());
    assertEquals("Must be test2.cfg", "test2.cfg", configFiles.get(1).getName());

    assertEquals("Must be 3", Value.INT_THREE, context.findVariableForName("globalvar1", true));
    assertEquals("Must be 'hello world'", Value.valueOf("hello world"), context.findVariableForName("globalvar2", true));
  }
}
