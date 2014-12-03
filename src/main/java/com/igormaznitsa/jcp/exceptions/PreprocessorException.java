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
  private final FilePositionInfo[] includeStack;

  public PreprocessorException(final String message, final String processedText, final FilePositionInfo[] includeStack, final Throwable cause) {
    super(message, cause);

    this.processingString = processedText;
    this.includeStack = includeStack == null ? new FilePositionInfo[0] : includeStack.clone();
  }

  public File getRootFile() {
    if (includeStack.length == 0) {
      return null;
    }
    else {
      return includeStack[includeStack.length - 1].getFile();
    }
  }

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

  public String getProcessingString() {
    return processingString;
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
    final StringBuilder result = new StringBuilder();
    result.append(getMessage()).append(", call stack: ").append(convertIncludeStackToString()).append(", source line: ").append(this.processingString);
    return result.toString();
  }
  
  public static PreprocessorException extractPreprocessorException(final Throwable thr){
    if (thr == null) return null;
    Throwable result = thr;
    do{
      if (result instanceof PreprocessorException) return (PreprocessorException)result;
      result = result.getCause();
    }while(result!=null);
    return null;
  }
}
