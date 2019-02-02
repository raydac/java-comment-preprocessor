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

package com.igormaznitsa.jcp.expression.functions;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;

import javax.annotation.Nonnull;

import static org.apache.commons.text.StringEscapeUtils.escapeJson;

/**
 * The class implements the str2json function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionSTR2JSON extends AbstractStrConverter {

  @Override
  @Nonnull
  public String getName() {
    return "str2json";
  }

  @Override
  @Nonnull
  public Value executeStr(@Nonnull final PreprocessorContext context, @Nonnull final Value value) {
    final String escaped = escapeJson(value.asString());
    return Value.valueOf(escaped);
  }

  @Override
  @Nonnull
  public String getReference() {
    return "escape string for JSON";
  }

  @Override
  @Nonnull
  public ValueType getResultType() {
    return ValueType.STRING;
  }
}
