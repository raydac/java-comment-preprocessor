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

import static com.igormaznitsa.jcp.utils.PreprocessorUtils.extractTail;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The handler processing the key to set the source directory
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class SourceDirectoryHandler implements CommandLineHandler {

  private static final String ARG_NAME = "/I:";

  @Override
  public String getDescription() {
    return "set source folder for preprocessing (default: " +
        PreprocessorContext.DEFAULT_SOURCE_DIRECTORY + ')';
  }

  @Override
  public boolean processCommandLineKey(final String key, final PreprocessorContext context) {
    boolean result = false;

    if (!key.isEmpty() && key.toUpperCase(Locale.ROOT).startsWith(ARG_NAME)) {
      final String tail = PreprocessorUtils.extractTrimmedTail(ARG_NAME, key);
      if (!tail.isEmpty()) {
        context.setSources(
            Arrays.stream(extractTail(ARG_NAME, key).split(Pattern.quote(File.pathSeparator)))
                .map(String::trim).collect(Collectors.toList()));
        result = true;
      }
    }
    return result;
  }

  @Override
  public String getKeyName() {
    return ARG_NAME;
  }
}
