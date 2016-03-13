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

import java.io.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.igormaznitsa.meta.annotation.MustNotContainNull;

/**
 * The exception allows to save some useful data about preprocessing files like
 * the current include stack and the error string index
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class PreprocessorException extends RuntimeException {

  private static final long serialVersionUID = 2857499664112391862L;

  private final String processingString;
  private transient final FilePositionInfo[] includeStack;

  public PreprocessorException(@Nullable final String message, @Nullable final String processedText, @Nullable @MustNotContainNull final FilePositionInfo[] includeStack, @Nullable final Throwable cause) {
    super(message, cause);

    this.processingString = processedText;
    this.includeStack = includeStack == null ? new FilePositionInfo[0] : includeStack.clone();
  }

  @Nullable
  public File getRootFile() {
    if (includeStack.length == 0) {
      return null;
    }
    else {
      return includeStack[includeStack.length - 1].getFile();
    }
  }

  @Nullable
  public File getProcessingFile() {
    if (includeStack.length == 0) {
      return null;
    }
    else {
      return includeStack[0].getFile();
    }
  }

  public int getStringIndex() {
    if (includeStack.length == 0) {
      return -1;
    }
    else {
      return includeStack[0].getStringIndex() + 1;
    }
  }

  @Nullable
  public String getProcessingString() {
    return processingString;
  }

  @Nullable
  private String convertIncludeStackToString() {
    final StringBuilder result = new StringBuilder();
    for (int i = 0; i < this.includeStack.length; i++) {
      if (i > 0) {
        result.append("<-");
      }
      result.append(this.includeStack[i].toString());
    }
    return result.toString();
  }

  @Nonnull
  @MustNotContainNull
  public FilePositionInfo[] getIncludeChain() {
    return this.includeStack.clone();
  }

  @Override
  @Nonnull
  public String toString() {
    final StringBuilder result = new StringBuilder();
    result.append(getMessage()).append(", include stack: ").append(convertIncludeStackToString()).append(", source line: ").append(this.processingString);
    return result.toString();
  }
  
  @Nonnull
  private static String makeStackView(@Nullable @MustNotContainNull final FilePositionInfo[] list, final char fill) {
    if (list == null || list.length == 0) {
      return "";
    }
    final StringBuilder builder = new StringBuilder();
    int tab = 5;

    for (int s = 0; s < tab; s++) {
      builder.append(fill);
    }
    builder.append("{File chain}");
    tab += 5;

    int fileIndex = 1;
    for (int i = list.length - 1; i >= 0; i--) {
      final FilePositionInfo cur = list[i];
      builder.append('\n');
      for (int s = 0; s < tab; s++) {
        builder.append(fill);
      }
      builder.append("└>");
      builder.append(fileIndex++).append(". ");
      builder.append(cur.getFile().getName()).append(':').append(cur.getStringIndex());
      tab += 3;
    }

    return builder.toString();
  }
  
  @Nullable
  public static PreprocessorException extractPreprocessorException(@Nullable final Throwable thr){
    if (thr == null) return null;
    Throwable result = thr;
    do{
      if (result instanceof PreprocessorException) return (PreprocessorException)result;
      result = result.getCause();
    }while(result!=null);
    return null;
  }

  @Nonnull
  public static String referenceAsString(final char fillChar, @Nullable final Throwable thr) {
    if (thr == null) {
      return "";
    }
    final StringWriter buffer = new StringWriter(1024);
    final PrintWriter out = new PrintWriter(buffer);
    final PreprocessorException pp = PreprocessorException.extractPreprocessorException(thr);
    if (pp == null) {
      out.println(thr.getMessage());
      thr.printStackTrace(out);
    }
    else {
      out.println(pp.getMessage());
      out.println(makeStackView(pp.getIncludeChain(),fillChar));
      if (pp.getCause() != null) {
        pp.getCause().printStackTrace(out);
      }
    }
    return buffer.toString();
  }

}
