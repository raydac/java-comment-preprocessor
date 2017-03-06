/* 
 * Copyright 2017 Igor Maznitsa (http://www.igormaznitsa.com).
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
package com.igormaznitsa.jcp.cmdline;

import java.io.File;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;

import java.util.Locale;

import javax.annotation.Nonnull;

/**
 * The Handler of subfolder names to be excluded from preprocessing, allows ANT pattern matching.
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class ExcludeFoldersHandler implements CommandLineHandler {

  private static final String ARG_NAME = "/ED:";

  @Override
  @Nonnull
  public String getDescription() {
    return "subfolders in source folders to be excluded from preprocessing, ANT patterns allowed, path separator should be used for multiple items";
  }

  @Override
  public boolean processCommandLineKey(@Nonnull final String key, @Nonnull final PreprocessorContext context) {
    boolean result = false;

    if (!key.isEmpty() && key.toUpperCase(Locale.ENGLISH).startsWith(ARG_NAME)) {
      final String tail = PreprocessorUtils.extractTrimmedTail(ARG_NAME, key);
      
      if (!tail.isEmpty()) {
        context.setExcludedFolderPatterns(tail.split("\\"+File.pathSeparator));
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
