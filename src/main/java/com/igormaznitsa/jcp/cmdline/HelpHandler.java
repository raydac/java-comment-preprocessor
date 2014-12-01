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
import java.util.Locale;

/**
 * The handler processes a help command from the command string
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class HelpHandler implements CommandLineHandler {

  private static final String[] ARG_NAMES = new String[]{"/H", "/?", "-H", "-?"};

  @Override
  public String getDescription() {
    return "print information about preprocessor usage and its directives";
  }

  @Override
  public boolean processCommandLineKey(final String key, final PreprocessorContext context) {
    boolean result = false;
    if (key != null && !key.isEmpty()) {

      final String argUpperCase = key.trim().toUpperCase(Locale.ENGLISH);

      for (final String str : ARG_NAMES) {
        if (str.equals(argUpperCase)) {
          result = true;
          break;
        }
      }
    }
    return result;
  }

  @Override
  public String getKeyName() {
    final StringBuilder result = new StringBuilder();
    for (int li = 0; li < ARG_NAMES.length; li++) {
      if (li > 0) {
        result.append(',');
      }
      result.append(ARG_NAMES[li]);
    }
    return result.toString();
  }
}
