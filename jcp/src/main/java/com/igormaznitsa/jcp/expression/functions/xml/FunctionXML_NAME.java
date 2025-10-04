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

package com.igormaznitsa.jcp.expression.functions.xml;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Element;

/**
 * The class implements the xml_name function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionXML_NAME extends AbstractXMLFunction {

  private static final List<List<ValueType>> ARG_TYPES = List.of(List.of(ValueType.STRING));

  @Override
  public String getName() {
    return "xml_name";
  }

  public Value executeStr(final PreprocessorContext context, final Value elementId) {
    final Element cachedElement = getCachedElement(context, elementId.asString());
    return Value.valueOf(cachedElement.getTagName());
  }

  @Override
  public Set<Integer> getArity() {
    return ARITY_1;
  }

  @Override
  public List<List<ValueType>> getAllowedArgumentTypes() {
    return ARG_TYPES;
  }

  @Override
  public String getReference() {
    return "get element name (tag)";
  }

  @Override
  public ValueType getResultType() {
    return ValueType.STRING;
  }

}
