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
 * The interface describes a logger to be used by a preprocessor during its work
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public interface PreprocessorLogger {

  /**
   * Log an error message
   *
   * @param message the text to be output into the error log
   */
  void error(String message);

  /**
   * Log an information message
   *
   * @param message the text to be output into the information log
   */
  void info(String message);

  /**
   * Log a warning message
   *
   * @param message the text to be output into the warning log
   */
  void warning(String message);
}
