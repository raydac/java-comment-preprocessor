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
  EXPRESSION("EXPR"),
  MULTI_EXPRESSION("EXPR1,EXPR2...EXPRn"),
  SET("VAR=EXPR"),
  ON_OFF("[+|-]");

  private final String str;

  DirectiveArgumentType(final String str) {
    this.str = str;
  }

  public String getAsText() {
    return this.str;
  }
}
