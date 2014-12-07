/*
 * Copyright 2014 Igor Maznitsa.
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

public class CareForLastNextLineCharHandler implements CommandLineHandler {

  private static final String ARG_NAME = "/N";

  @Override
  public String getDescription() {
    return "accurate reproduction of the last line end char state (not the next line char!)";
  }

  @Override
  public boolean processCommandLineKey(final String key, final PreprocessorContext context) {
    boolean result = false;

    if (ARG_NAME.equalsIgnoreCase(key)) {
      context.setCareForLastNextLine(true);
      result = true;
    }

    return result;
  }

  @Override
  public String getKeyName() {
    return ARG_NAME;
  }
  
}
