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

package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * The handler for the preprocessing file extension list (with comma)
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class FileExtensionsHandler implements CommandLineHandler {

  private static final String ARG_NAME = "/F:";

  @Override
  @Nonnull
  public String getDescription() {
    return "comma separated list of allowed extensions (case insensitive) (by default " + PreprocessorContext.DEFAULT_PROCESSING_EXTENSIONS.stream().collect(Collectors.joining(",")) + ')';
  }

  @Override
  public boolean processCommandLineKey(@Nonnull final String key, @Nonnull final PreprocessorContext context) {
    boolean result = false;

    if (!key.isEmpty() && key.toUpperCase(Locale.ENGLISH).startsWith(ARG_NAME)) {
      final String extensions = PreprocessorUtils.extractTrimmedTail(ARG_NAME, key);

      if (!extensions.isEmpty()) {
        context.setProcessingFileExtensions(Arrays.stream(extensions.split("\\,")).map(String::trim).collect(Collectors.toList()));
        result = true;
      }
    }

    return result;
  }

  @Override
  @Nonnull
  public String getKeyName() {
    return ARG_NAME;
  }
}
