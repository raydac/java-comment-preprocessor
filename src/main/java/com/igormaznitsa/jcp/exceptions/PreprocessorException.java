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

import java.io.File;

/**
 * The exception allows to save some useful data about preprocessing files like
 * the current include stack and the error string index
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class PreprocessorException extends Exception {

  private static final long serialVersionUID = 2857499664112391862L;
  private final String processingString;
  private final FilePositionInfo[] callStack;

  public PreprocessorException(final String message, final String processinText, final FilePositionInfo[] callStack, final Throwable cause) {
    super(message, cause);

    this.processingString = processinText;
    this.callStack = callStack == null ? new FilePositionInfo[0] : callStack.clone();
  }

  public File getRootFile() {
    if (callStack.length == 0) {
      return null;
    }
    else {
      return callStack[callStack.length - 1].getFile();
    }
  }

  public File getProcessingFile() {
    if (callStack.length == 0) {
      return null;
    }
    else {
      return callStack[0].getFile();
    }
  }

  public int getStringIndex() {
    if (callStack.length == 0) {
      return -1;
    }
    else {
      return callStack[0].getStringIndex() + 1;
    }
  }

  public String getProcessingString() {
    return processingString;
  }

  private String makeCallStackAsString() {
    final StringBuilder result = new StringBuilder();
    for (int i = 0; i < callStack.length; i++) {
      if (i > 0) {
        result.append("<-");
      }
      result.append(callStack[i].toString());
    }
    return result.toString();
  }

  public FilePositionInfo[] getFileStack() {
    return callStack.clone();
  }

  @Override
  public String toString() {
    final StringBuilder result = new StringBuilder();

    result.append(getMessage()).append(' ').append(processingString).
            append(' ').append('[').append(makeCallStackAsString()).append(']');

    return result.toString();
  }
}
