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
package com.igormaznitsa.jcp.directives;

public class LinesNotMatchException extends RuntimeException {

  private static final long serialVersionUID = 0x129894723894A123L;

  private final int etalonLineNumber;
  private final int resultLineNumber;
  private final int problemStringIndex;
  private final String etalonString;
  private final String resultString;

  public LinesNotMatchException(final int etalonLineNumber, final int resultLineNumber, final int problemStringIndex, final String etalonString, final String resultString) {
    super("Lines not match in the etalon and the result");
    this.etalonLineNumber = etalonLineNumber;
    this.resultLineNumber = resultLineNumber;
    this.etalonString = etalonString;
    this.resultString = resultString;
    this.problemStringIndex = problemStringIndex;
  }

  public int getProblemStringIndex() {
    return this.problemStringIndex;
  }

  public int getEtalonLineNumber() {
    return this.etalonLineNumber;
  }

  public int getResultLineNumber() {
    return this.resultLineNumber;
  }

  public String getEtalonString() {
    return this.etalonString;
  }

  public String getResultString() {
    return this.resultString;
  }

  @Override
  public String toString() {
    return LinesNotMatchException.class.getName() + "(etalonLineNum=" + this.etalonLineNumber
            + ",resultLineNum=" + this.resultLineNumber
            + ",problemLine" + (this.problemStringIndex + 1)
            + ",etalonString=" + this.etalonString
            + ",resultString=" + this.resultString
            + ')';
  }
}
