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

import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;
import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.igormaznitsa.meta.annotation.MustNotContainNull;

/**
 * The class contains text data of a file and the string position index for the file
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class TextFileDataContainer {

  private final String[] text;
  private final boolean fileEndedByNextLine;
  private final File file;

  /**
   * Flag shows to save automatically buffers after file preprocessing end.
   */
  private boolean autoFlush = true;
  private int nextStringIndex;

  public void disableAutoFlush() {
    this.autoFlush = false;
  }

  public boolean isAutoFlush() {
    return this.autoFlush;
  }

  @Nonnull
  @MustNotContainNull
  public String[] getText() {
    return text.clone();
  }

  @Nonnull
  public File getFile() {
    return file;
  }

  public void reset() {
    nextStringIndex = 0;
  }

  public boolean isPresentedNextLineOnReadString() {
    return this.nextStringIndex < this.text.length || fileEndedByNextLine;
  }

  @Nullable
  public String nextLine() {
    if (nextStringIndex >= text.length) {
      return null;
    } else {
      return text[nextStringIndex++];
    }
  }

  public void setNextStringIndex(final int index) {
    if (index < 0 || index >= text.length) {
      throw new IndexOutOfBoundsException("String index out of bound [" + index + ']');
    }
    this.nextStringIndex = index;
  }

  public int getLastReadStringIndex() {
    return nextStringIndex - 1;
  }

  public int getNextStringIndex() {
    return nextStringIndex;
  }

  public TextFileDataContainer(@Nonnull final TextFileDataContainer item, final int stringIndex) {
    this(item.file, item.text, item.fileEndedByNextLine, stringIndex);
  }

  public TextFileDataContainer(@Nonnull final File currentFile, @Nonnull @MustNotContainNull final String[] text, final boolean fileEndedByNextLine, final int stringIndex) {
    assertNotNull("File is null", currentFile);
    assertNotNull("Text is null", text);
    this.file = currentFile;
    this.text = text;
    setNextStringIndex(stringIndex);
    this.fileEndedByNextLine = fileEndedByNextLine;
  }

  @Override
  public int hashCode() {
    return file.hashCode();
  }

  @Override
  public boolean equals(@Nullable final Object that) {
    if (that == null) {
      return false;
    }

    if (this == that) {
      return true;
    }

    if (that instanceof TextFileDataContainer) {
      final TextFileDataContainer thatItem = (TextFileDataContainer) that;
      return file.equals(thatItem.file) && nextStringIndex == thatItem.nextStringIndex;
    }
    return false;
  }
}
