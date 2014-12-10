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
 * To set the output text character encoding
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class OutCharsetHandler implements CommandLineHandler {

  private static final String ARG_NAME = "/TT:";

  @Override
  public String getKeyName() {
    return ARG_NAME;
  }

  @Override
  public String getDescription() {
    return "set the output encoding for text files (by default " + PreprocessorContext.DEFAULT_CHARSET + ')';
  }

  @Override
  public boolean processCommandLineKey(final String key, final PreprocessorContext context) {

    boolean result = false;

    if (key != null && key.toUpperCase(Locale.ENGLISH).startsWith(ARG_NAME)) {
      final String value = PreprocessorUtils.extractTrimmedTail(ARG_NAME, key);

      if (!value.isEmpty()) {
        context.setOutCharacterEncoding(value);
        result = true;
      }
    }
    return result;
  }
}
