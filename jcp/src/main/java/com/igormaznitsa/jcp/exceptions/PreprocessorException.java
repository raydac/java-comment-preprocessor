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

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * The exception allows to save some useful data about preprocessing files like the current include stack and the error string index
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class PreprocessorException extends RuntimeException {

  private static final long serialVersionUID = 2857499664112391862L;

  private final String processingString;
  private final transient FilePositionInfo[] includeStack;

  public PreprocessorException(final String message, final String processedText,
                               final FilePositionInfo[] includeStack, final Throwable cause) {
    super(message, cause);

    this.processingString = processedText;
    this.includeStack = includeStack == null ? new FilePositionInfo[0] : includeStack.clone();
  }


  private static String makeStackView(final FilePositionInfo[] list, final char fill) {
    if (list == null || list.length == 0) {
      return "";
    }
    final StringBuilder builder = new StringBuilder();
    int tab = 5;

    builder.append(String.valueOf(fill).repeat(tab));
    builder.append("{File chain}");
    tab += 5;

    int fileIndex = 1;
    for (int i = list.length - 1; i >= 0; i--) {
      final FilePositionInfo cur = list[i];
      builder.append('\n');
      builder.append(String.valueOf(fill).repeat(Math.max(0, tab)));
      builder.append("└>");
      builder.append(fileIndex++).append(". ");
      builder.append(cur.getFile().getName()).append(':').append(cur.getLineNumber());
      tab += 3;
    }

    return builder.toString();
  }


  public static PreprocessorException extractPreprocessorException(final Throwable thr) {
    if (thr == null) {
      return null;
    }
    Throwable result = thr;
    do {
      if (result instanceof PreprocessorException) {
        return (PreprocessorException) result;
      }
      result = result.getCause();
    } while (result != null);
    return null;
  }


  public static String referenceAsString(final char fillChar, final Throwable thr) {
    if (thr == null) {
      return "";
    }
    final StringWriter buffer = new StringWriter(1024);
    final PrintWriter out = new PrintWriter(buffer);
    final PreprocessorException pp = PreprocessorException.extractPreprocessorException(thr);
    if (pp == null) {
      out.println(thr.getMessage());
      thr.printStackTrace(out);
    } else {
      out.println(pp.getMessage());
      out.println(makeStackView(pp.getIncludeChain(), fillChar));
      if (pp.getCause() != null) {
        pp.getCause().printStackTrace(out);
      }
    }
    return buffer.toString();
  }


  public File getRootFile() {
    if (includeStack.length == 0) {
      return null;
    } else {
      return includeStack[includeStack.length - 1].getFile();
    }
  }


  public File getProcessingFile() {
    if (includeStack.length == 0) {
      return null;
    } else {
      return includeStack[0].getFile();
    }
  }

  public int getLineNumber() {
    if (includeStack.length == 0) {
      return -1;
    } else {
      return includeStack[0].getLineNumber();
    }
  }


  public String getProcessingString() {
    return this.processingString;
  }


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

  public FilePositionInfo[] getIncludeChain() {
    return this.includeStack.clone();
  }

  @Override
  public String toString() {
    return getMessage() + ", include stack: " + convertIncludeStackToString() + ", source line: " +
        this.processingString;
  }

}
