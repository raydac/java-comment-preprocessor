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
package com.igormaznitsa.jcp.expression.functions.xml;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;

/**
 * The class implements the xml_get function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionXML_GET extends AbstractXMLFunction {

  private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING, ValueType.INT}};

  @Override
  public String getName() {
    return "xml_get";
  }

  public Value executeStrInt(final PreprocessorContext context, final Value elementListId, final Value elementIndex) {
    return Value.valueOf(findElementForIndex(context, elementListId.asString(), elementIndex.asLong().intValue()));
  }

  @Override
  public int getArity() {
    return 2;
  }

  @Override
  public ValueType[][] getAllowedArgumentTypes() {
    return ARG_TYPES;
  }

  @Override
  public String getReference() {
    return "get element from element list by its index (first 0)";
  }

  @Override
  public ValueType getResultType() {
    return ValueType.STRING;
  }
}
