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
import org.w3c.dom.NodeList;

/**
 * The class implements the xml_list function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionXML_LIST extends AbstractXMLFunction {

  private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING, ValueType.STRING}};

  @Override
  public String getName() {
    return "xml_list";
  }

  public Value executeStrStr(final PreprocessorContext context, final Value elementId, final Value elementTag) {
    final String tagName = elementTag.asString();
    final Element element = getCachedElement(context, elementId.asString());
    final String listId = makeElementListId(element, tagName);
    
    NodeContainer container = (NodeContainer) context.getSharedResource(listId);
    if (container == null) {
      final NodeList list = element.getElementsByTagName(tagName);
      container = new NodeContainer(UID_COUNTER.getAndIncrement(), list);
      context.setSharedResource(listId, container);
    }

    return Value.valueOf(listId);
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
    return "get element list by element tag";
  }

  @Override
  public ValueType getResultType() {
    return ValueType.STRING;
  }
}
