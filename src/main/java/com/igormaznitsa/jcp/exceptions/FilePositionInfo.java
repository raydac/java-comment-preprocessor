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

package com.igormaznitsa.jcp.exceptions;

import com.igormaznitsa.jcp.utils.PreprocessorUtils;

import javax.annotation.Nonnull;
import java.io.File;

import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

/**
 * The class implements a file data storage where an exception can store a
 * snapshot of the current preprocessing file data
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class FilePositionInfo {

  /**
   * The preprocessing file
   */
  private final File file;

  /**
   * The current string index in the file
   */
  private final int stringIndex;

  public FilePositionInfo(@Nonnull final File file, final int stringIndex) {
    assertNotNull("File is null", file);
    this.file = file;
    this.stringIndex = stringIndex;
  }

  @Nonnull
  public File getFile() {
    return this.file;
  }

  public int getStringIndex() {
    return this.stringIndex;
  }

  @Override
  @Nonnull
  public String toString() {
    final String filePath = PreprocessorUtils.getFilePath(this.file);
    return filePath + ':' + Integer.toString(stringIndex + 1);
  }
}
