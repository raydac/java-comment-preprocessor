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

package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;

/**
 * The interface describes a command line key handler. It is not just a handler,
 * but it will be called for all met keys to recognize one to be processed.
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public interface CommandLineHandler {

  /**
   * Get the key name for the handler
   *
   * @return the key name as a String, must not be null
   */
  String getKeyName();

  /**
   * Get the description of the key (it will be printed into the help text)
   *
   * @return the description as a String
   */
  String getDescription();

  /**
   * Process a command line key
   *
   * @param key     the command line key to be processed, must not be null
   * @param context the preprocessor context, must not be null
   * @return true if the key has been recognized and processed else false
   */
  boolean processCommandLineKey(String key, PreprocessorContext context);
}
