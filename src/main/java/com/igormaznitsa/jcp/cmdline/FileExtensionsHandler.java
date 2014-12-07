/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
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

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.util.Locale;

/**
 * The handler for the preprocessing file extension list (with comma)
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class FileExtensionsHandler implements CommandLineHandler {

  private static final String ARG_NAME = "/F:";

  @Override
  public String getDescription() {
    return "set (case insensitive) preprocessing file extension list (with comma) (by default " + PreprocessorContext.DEFAULT_PROCESSING_EXTENSIONS + ')';
  }

  @Override
  public boolean processCommandLineKey(final String key, final PreprocessorContext context) {
    boolean result = false;

    if (key != null && !key.isEmpty()) {

      if (key.toUpperCase(Locale.ENGLISH).startsWith(ARG_NAME)) {
        final String extensions = PreprocessorUtils.extractTrimmedTail(ARG_NAME, key);

        if (!extensions.isEmpty()) {
          context.setProcessingFileExtensions(extensions);
          result = true;
        }
      }
    }
    return result;
  }

  @Override
  public String getKeyName() {
    return ARG_NAME;
  }
}
