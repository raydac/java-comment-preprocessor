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

package com.igormaznitsa.jcp;

import static org.apache.commons.io.FilenameUtils.normalize;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.containers.TextFileDataContainer;
import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PreprocessorContext.class, PreprocessingState.class})
public abstract class AbstractSpyPreprocessorContextTest {

  protected static TemporaryFolder destinationFolder;

  @BeforeClass
  public static void prepareClassTests() throws Exception {
    destinationFolder = new TemporaryFolder(new File("./"));
    destinationFolder.create();
  }

  @AfterClass
  public static void doJanitor() throws Exception {
    destinationFolder.delete();
  }

  @Before
  public void beforeTest() throws Exception {
    FileUtils.cleanDirectory(destinationFolder.getRoot());
  }

  protected List<String> getCurrentTestFolder() {
    final String testFolder =
        FilenameUtils.normalizeNoEndSeparator(System.getProperty("test.folder"));
    final String fullClassPath = this.getClass().getName().replace('.', File.separatorChar);
    return Collections.singletonList(normalize(testFolder + File.separator +
        fullClassPath.substring(0, fullClassPath.lastIndexOf(File.separatorChar))));
  }

  protected File getDestinationFolder() {
    return destinationFolder.getRoot();
  }

  protected void assertDestinationFolderEmpty() throws Exception {
    assertEquals("Destination folder must be enpty", 0, destinationFolder.getRoot().list().length);
  }

  protected PreprocessorContext preparePreprocessorContext(final List<String> sourceFolders)
      throws Exception {
    return this.preparePreprocessorContext(sourceFolders, () -> false);
  }

  protected PreprocessorContext preparePreprocessorContext(final List<String> sourceFolders,
                                                           final ContextDataProvider provider)
      throws Exception {
    final PreprocessorContext resultContext =
        PowerMockito.spy(new PreprocessorContext(new File("some_impossible_folder_121212")));
    final PreprocessingState fakeState = PreprocessingState.makeFake(resultContext);
    PowerMockito.when(resultContext.findFileInfoContainer(any(File.class)))
        .then(x ->
            Optional.ofNullable(provider.findFileInfoContainer(x.getArgument(0))));

    PowerMockito.when(resultContext.getPreprocessingState()).thenReturn(fakeState);

    resultContext.setAllowWhitespaces(provider.getAllowSpaceBeforeDirectiveFlag());
    resultContext.setSources(sourceFolders);
    resultContext.setTarget(destinationFolder.getRoot());

    return resultContext;
  }

  public interface ContextDataProvider {

    boolean getAllowSpaceBeforeDirectiveFlag();

    default Optional<TextFileDataContainer> findLastTextFileDataContainerInStack() {
      return Optional.of(
          new TextFileDataContainer(new File(PreprocessingState.FAKE_FILE_PATH), new String[] {""},
              false, 0));
    }

    default FileInfoContainer findFileInfoContainer(File file) {
      return new FileInfoContainer(file, file.getName(), false);
    }
  }

}
