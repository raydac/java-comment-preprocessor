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

/**
 * The class implements the IS function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionIS extends AbstractFunction {

  private static final ValueType[][] SIGNATURES =
      new ValueType[][] {{ValueType.STRING, ValueType.ANY}};

  @Override

  public String getName() {
    return "is";
  }


  public Value executeStrAny(final PreprocessorContext context, final Value varName,
                             final Value value) {
    final Value currentValue = context.findVariableForName(varName.asString(), false);

    Value result = Value.BOOLEAN_FALSE;

    if (currentValue != null) {
      result = value.toString().compareTo(currentValue.toString()) == 0 ? Value.BOOLEAN_TRUE :
          Value.BOOLEAN_FALSE;
    }

    return result;
  }

  @Override
  public int getArity() {
    return 2;
  }

  @Override


  public ValueType[][] getAllowedArgumentTypes() {
    return SIGNATURES;
  }

  @Override

  public String getReference() {
    return "check that string is string form of value";
  }

  @Override

  public ValueType getResultType() {
    return ValueType.BOOLEAN;
  }

}
