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
package com.igormaznitsa.jcp.logger;

/**
 * An Easy logger which just output log messages into the system output streams
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class SystemOutLogger implements PreprocessorLogger {

  @Override
  public void error(final String text) {
    if (text != null) {
      final String out = "[JCP.ERR] " + text;
      System.err.println(out);
    }
  }

  @Override
  public void info(final String text) {
    if (text != null) {
      final String out = "[JCP.INFO] " + text;
      System.out.println(out);
    }
  }

  @Override
  public void warning(final String text) {
    if (text != null) {
      final String out = "[JCP.WARN] " + text;
      System.out.println(out);
    }
  }
}
