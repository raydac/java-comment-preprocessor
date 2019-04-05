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
import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.meta.annotation.MayContainNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;

public class SpacesBeforeDirectivesNotAllowedTest extends AbstractUseCaseTest {

  private static class WarnLogPreprocessorContext extends PreprocessorContext {
    private final List<String> warnings = new CopyOnWriteArrayList<>();

    public WarnLogPreprocessorContext(@Nonnull final File baseDir) {
      super(baseDir);
    }

    @Nonnull
    @MayContainNull
    public List<String> getWarnings() {
      return this.warnings;
    }

    @Override
    public void logWarning(@Nullable String text) {
      this.warnings.add(text);
      super.logWarning(text);
    }
  }

  @Override
  protected PreprocessorContext createPreprocessorContext(@Nonnull final File baseFolder) {
    return new WarnLogPreprocessorContext(baseFolder);
  }

  @Override
  protected void tuneContext(@Nonnull final PreprocessorContext context) {
    context.setAllowWhitespaces(false);
  }

  @Override
  public void check(@Nonnull final PreprocessorContext context, @Nonnull final JcpPreprocessor.Statistics stat) throws Exception {
    assertEquals(1, stat.getPreprocessed());
    assertEquals(0, stat.getCopied());

    final WarnLogPreprocessorContext warnContext = (WarnLogPreprocessorContext) context;

    assertEquals(3, warnContext.getWarnings().stream().filter(x -> x != null && x.startsWith(FileInfoContainer.WARNING_SPACE_BEFORE_HASH)).count());
  }

}
