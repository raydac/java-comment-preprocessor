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

import static org.junit.Assert.assertEquals;


import com.igormaznitsa.jcp.JcpPreprocessor;
import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SpacesBeforeDirectivesTest extends AbstractUseCaseTest {

  @Override
  protected PreprocessorContext createPreprocessorContext(final File baseFolder) {
    return new WarnLogPreprocessorContext(baseFolder);
  }

  @Override
  protected void tuneContext(final PreprocessorContext context) {
    context.setAllowWhitespaces(true);
  }

  @Override
  public void check(final PreprocessorContext context, final JcpPreprocessor.Statistics stat)
      throws Exception {
    assertEquals(1, stat.getPreprocessed());
    assertEquals(0, stat.getCopied());

    final WarnLogPreprocessorContext warnContext = (WarnLogPreprocessorContext) context;

    assertEquals(0, warnContext.getWarnings().stream()
        .filter(x -> x != null && x.startsWith(FileInfoContainer.WARNING_SPACE_BEFORE_HASH))
        .count());
  }

  private static class WarnLogPreprocessorContext extends PreprocessorContext {
    private final List<String> warnings = new CopyOnWriteArrayList<>();

    public WarnLogPreprocessorContext(final File baseDir) {
      super(baseDir);
    }

    public List<String> getWarnings() {
      return this.warnings;
    }

    @Override
    public void logWarning(String text) {
      this.warnings.add(text);
      super.logWarning(text);
    }
  }

}
