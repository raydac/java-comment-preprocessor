/*
 * Copyright 2002-2019 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.igormaznitsa.jcp.logger;

import javax.annotation.Nullable;

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
  void error(@Nullable String message);

  /**
   * Log an information message
   *
   * @param message the text to be output into the information log
   */
  void info(@Nullable String message);

  /**
   * Log a debug message
   *
   * @param message the text to be output into the information log
   * @since 6.0.1
   */
  void debug(@Nullable String message);

  /**
   * Log a warning message
   *
   * @param message the text to be output into the warning log
   */
  void warning(@Nullable String message);
}
