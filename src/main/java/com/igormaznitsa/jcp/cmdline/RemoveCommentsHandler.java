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

/**
 * The handler processing the flag to clear all sources in the destination
 * directory from inside comments
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class RemoveCommentsHandler implements CommandLineHandler {

  private static final String ARG_NAME = "/R";

  @Override
  public String getDescription() {
    return "remove comments from result files";
  }

  @Override
  public boolean processCommandLineKey(final String argument, final PreprocessorContext configurator) {
    boolean result = false;

    if (ARG_NAME.equalsIgnoreCase(argument)) {
      configurator.setRemoveComments(true);
      result = true;
    }

    return result;
  }

  @Override
  public String getKeyName() {
    return ARG_NAME;
  }
}
