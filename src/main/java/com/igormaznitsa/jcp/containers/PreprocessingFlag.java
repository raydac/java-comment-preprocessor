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
package com.igormaznitsa.jcp.containers;

/**
 * The enumeration contains flags describe inside special preprocessor states
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public enum PreprocessingFlag {

  /**
   * This flag shows that it is allowed to print texts into an output stream
   */
  TEXT_OUTPUT_DISABLED,
  /**
   * This flag shows that we must comment the next line (one time flag)
   */
  COMMENT_NEXT_LINE,
  /**
   * This flag shows that the current //#if construction in the passive state
   */
  IF_CONDITION_FALSE,
  /**
   * This flag shows that //#break has been met
   */
  BREAK_COMMAND,
  /**
   * This flag shows that preprocessing must be ended on the next string
   */
  END_PROCESSING,
  /**
   * This flag allows to stop preprocessing immediately
   */
  ABORT_PROCESSING
}
