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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The class implements the xml_getroot function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionXML_GETROOT extends AbstractFunction {

  private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING}};

  @Override
  public String getName() {
    return "xml_getroot";
  }

  public Value executeStr(final PreprocessorContext context, final Value docId) {
    final String docIdStr = docId.asString();
    final String docIdStrRoot = docIdStr + "_root";

    final Value result = Value.valueOf(docIdStrRoot);
    NodeContainer nodeContainer = null;
    try {
      nodeContainer = (NodeContainer) context.getSharedResource(docIdStrRoot);
    }
    catch (ClassCastException ex) {
      throw new IllegalArgumentException("Wrong type of the cached object [" + docIdStrRoot + ']');
    }

    if (nodeContainer == null) {
      try {
        nodeContainer = (NodeContainer) context.getSharedResource(docIdStr);
      }
      catch (ClassCastException ex) {
        throw new IllegalArgumentException("Incomatible type of cached document [" + docIdStr + ']');
      }

      if (nodeContainer == null) {
        throw new IllegalArgumentException("Can't find any opened xml document for the \'" + docIdStr + "\' id");
      }

      final Document doc = (Document) nodeContainer.getNode();
      final Element rootElement = doc.getDocumentElement();

      final NodeContainer rootContainer = new NodeContainer(UID_COUNTER.getAndIncrement(), rootElement);
      context.setSharedResource(docIdStrRoot, rootContainer);
    }

    return result;
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
    return "it returns the root element of an opened xml document";
  }

  @Override
  public ValueType getResultType() {
    return ValueType.STRING;
  }
}
