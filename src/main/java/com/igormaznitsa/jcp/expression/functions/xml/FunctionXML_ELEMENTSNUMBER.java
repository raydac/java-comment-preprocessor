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
import com.igormaznitsa.jcp.expression.functions.AbstractFunction;
import org.w3c.dom.NodeList;

/**
 * The class implements the xml_elementsnumber function
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionXML_ELEMENTSNUMBER extends AbstractFunction {

  private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING}};

  @Override
  public String getName() {
    return "xml_elementsnumber";
  }

  public Value executeStr(final PreprocessorContext context, final Value listId) {
    final String elementIdStr = listId.asString();

    final NodeContainer container = (NodeContainer) context.getSharedResource(elementIdStr);
    if (container == null || container.getNodeList() == null) {
      throw new IllegalArgumentException("Can't find any element list for the \'" + elementIdStr + "\' id");
    }

    final NodeList list = container.getNodeList();
    return Value.valueOf(Long.valueOf(list.getLength()));
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
    return "it returns the length of an element list";
  }

  @Override
  public ValueType getResultType() {
    return ValueType.INT;
  }

}
