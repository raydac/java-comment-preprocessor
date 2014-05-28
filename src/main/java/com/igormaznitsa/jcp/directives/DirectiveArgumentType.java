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

/**
 * The enumeration contains possible argument types are being used by directives
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public enum DirectiveArgumentType {

  NONE(""),
  STRING("STRING"),
  TAIL("TAIL"),
  BOOLEAN("BOOLEAN"),
  VARNAME("VAR"),
  EXPRESSTION("EXPR"),
  MULTIEXPRESSION("EXPR1,EXPR2...EXPRn"),
  SET("VAR=EXPR"),
  ONOFF("[+|-]");

  private final String str;

  private DirectiveArgumentType(final String str) {
    this.str = str;
  }

  public String getAsText() {
    return this.str;
  }
}
