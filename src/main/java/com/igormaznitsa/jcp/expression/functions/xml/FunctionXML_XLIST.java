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
import javax.xml.xpath.*;
import org.w3c.dom.*;

/**
 * The class implements the xml_xpathlist function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionXML_XLIST extends AbstractXMLFunction {

  private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING, ValueType.STRING}};

  @Override
  public String getName() {
    return "xml_xlist";
  }

  public Value executeStrStr(final PreprocessorContext context, final Value documentId, final Value xPath) {
    final String documentIdStr = documentId.asString();
    final String pathStr = xPath.asString();

    final String xpathId = makeXPathListId(documentIdStr, pathStr);
    final Document document = getCachedDocument(context, documentIdStr);
    
    NodeList list = findCachedElementList(context, xpathId);
    if (list == null){
      try {
        final XPathExpression expression = prepareXPathExpression(pathStr);
        list = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
      }
      catch (XPathExpressionException ex) {
        throw context.makeException("Error during XPath compilation [" + pathStr + ']',ex);
      }
      final NodeContainer container = new NodeContainer(UID_COUNTER.getAndIncrement(), list);
      context.setSharedResource(xpathId, container);
    }
    return Value.valueOf(xpathId);
  }

  private XPathExpression prepareXPathExpression(final String path) throws XPathExpressionException {
    final XPathFactory factory = XPathFactory.newInstance();
    final XPath xpath = factory.newXPath();
    return xpath.compile(path);
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
    return "get element list for XPath";
  }

  @Override
  public ValueType getResultType() {
    return ValueType.STRING;
  }
}
