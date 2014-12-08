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
import org.w3c.dom.Element;

/**
 * The class implements the xml_name function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionXML_NAME extends AbstractXMLFunction {

  private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING}};

  @Override
  public String getName() {
    return "xml_name";
  }

  public Value executeStr(final PreprocessorContext context, final Value elementId) {
    final Element cachedelement = getCachedElement(context, elementId.asString());
    return Value.valueOf(cachedelement.getTagName());
  }

  @Override
  public int getArity() {
    return 1;
  }

  @Override
  public ValueType[][] getAllowedArgumentTypes() {
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
