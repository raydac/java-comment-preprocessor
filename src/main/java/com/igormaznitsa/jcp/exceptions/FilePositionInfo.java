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
package com.igormaznitsa.jcp.exceptions;

import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.File;

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

  public FilePositionInfo(final File file, final int stringIndex) {
    PreprocessorUtils.assertNotNull("File is null", file);
    this.file = file;
    this.stringIndex = stringIndex;
  }

  public File getFile() {
    return this.file;
  }

  public int getStringIndex() {
    return this.stringIndex;
  }

  @Override
  public String toString() {
    final String filePath = PreprocessorUtils.getFilePath(this.file);
    return filePath + ':' + Integer.toString(stringIndex + 1);
  }
}
