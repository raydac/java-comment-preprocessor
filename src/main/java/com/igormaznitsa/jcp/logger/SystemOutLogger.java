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

import javax.annotation.Nullable;

/**
 * An Easy logger which just output log messages into the system output streams
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class SystemOutLogger implements PreprocessorLogger {
  
  /**
   * Name of system property to enable debug level logging.
   */
  public static final String PROPERTY_DEBUG_FLAG = "jcp.log.debug";

  private static final boolean FLAG_DEBUG_LEVEL = Boolean.parseBoolean(System.getProperty(PROPERTY_DEBUG_FLAG));


  @Override
  public int hashCode() {
    return System.out.hashCode();
  }
  
  @Override
  public boolean equals(@Nullable final Object value) {    
    return value!=null && value instanceof SystemOutLogger;
  }
  
  @Override
  public void error(@Nullable final String text) {
    if (text != null) {
      final String out = "[JCP.ERR] " + text;
      System.err.println(out);
    }
  }

  @Override
  public void info(@Nullable final String text) {
    if (text != null) {
      final String out = "[JCP.INFO] " + text;
      System.out.println(out);
    }
  }

  @Override
  public void warning(@Nullable final String text) {
    if (text != null) {
      final String out = "[JCP.WARN] " + text;
      System.out.println(out);
    }
  }

  @Override
  public void debug(@Nullable final String text) {
    if (FLAG_DEBUG_LEVEL && text != null) {
      final String out = "[JCP.DEBUG] " + text;
      System.out.println(out);
    }
  }
}
