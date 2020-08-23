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
import com.igormaznitsa.jcp.expression.functions.AbstractFunction;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.xpath.jaxp.XPathFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractXMLFunction extends AbstractFunction {


  public static String makeElementListId(final Element parentName, final String elementName) {
    return buildPathForElement(parentName) + "_#list_" + elementName;
  }


  public static String makeDocumentId(final String fileName) {
    return "xmlDocument_" + fileName;
  }


  public static String makeDocumentRootId(final String documentId) {
    return documentId + "_#root";
  }


  public static String makeElementId(final String elementListId, final int elementIndex) {
    return elementListId + '_' + elementIndex;
  }


  public static String makeXPathListId(final String documentId, final String xpath) {
    return documentId + "_#xpath_" + xpath;
  }


  public static String makeXPathElementId(final String documentId, final String xpath) {
    return documentId + "_#xpathelement_" + xpath;
  }


  public static String buildPathForElement(final Element element) {
    final StringBuilder result = new StringBuilder();

    Node theNode = element;

    while (theNode != null) {
      int level = 0;
      Node sibling = theNode;
      while (sibling != null) {
        level++;
        sibling = sibling.getPreviousSibling();
      }

      result.append('/').append(theNode.getNodeName()).append('{').append(level).append('}');
      theNode = theNode.getParentNode();
    }

    return result.toString();
  }


  public static String getFirstLevelTextContent(final Node node) {
    final NodeList list = node.getChildNodes();
    final StringBuilder textContent = new StringBuilder(128);
    for (int i = 0; i < list.getLength(); ++i) {
      final Node child = list.item(i);
      if (child.getNodeType() == Node.TEXT_NODE) {
        textContent.append(child.getTextContent());
      }
    }
    return textContent.toString();
  }


  protected static XPathExpression prepareXPathExpression(final String path)
      throws XPathExpressionException {
    final XPathFactory factory = new XPathFactoryImpl();
    final XPath xpath = factory.newXPath();
    return xpath.compile(path);
  }


  public String getAttribute(final PreprocessorContext context, final String elementId,
                             final String attributeName) {
    final NodeContainer container = (NodeContainer) context.getSharedResource(elementId);
    if (container == null) {
      throw context
          .makeException("Can't find any active element with the '" + elementId + "' id", null);
    }
    try {
      return ((Element) container.getNode()).getAttribute(attributeName);
    } catch (ClassCastException ex) {
      throw context.makeException(
          "Incompatible cached element type [" + elementId + '.' + attributeName + ']', ex);
    }
  }


  public Document getCachedDocument(final PreprocessorContext context, final String documentId) {
    final NodeContainer container = (NodeContainer) context.getSharedResource(documentId);
    if (container == null) {
      throw context
          .makeException("Can't find any document for the '" + documentId + "' id", null);
    }

    try {
      return (Document) container.getNode();
    } catch (ClassCastException ex) {
      throw context.makeException("Incompatible cached element type [" + documentId + ']', ex);
    }
  }


  public Element findCachedElement(final PreprocessorContext context, final String elementId) {
    final NodeContainer container = (NodeContainer) context.getSharedResource(elementId);
    if (container == null) {
      return null;
    }

    try {
      return (Element) container.getNode();
    } catch (ClassCastException ex) {
      throw context.makeException("Incompatible cached element type [" + elementId + ']', null);
    }
  }


  public Element getCachedElement(final PreprocessorContext context, final String elementId) {
    final Element element = findCachedElement(context, elementId);
    if (element == null) {
      throw context
          .makeException("Can't find any active element for the '" + elementId + "' id", null);
    }
    return element;
  }


  public NodeList findCachedElementList(final PreprocessorContext context,
                                        final String elementListId) {
    final NodeContainer container = (NodeContainer) context.getSharedResource(elementListId);
    if (container == null) {
      return null;
    }
    try {
      return container.getNodeList();
    } catch (ClassCastException ex) {
      throw context.makeException("Incompatible cached element type [" + elementListId + ']', ex);
    }
  }


  public NodeList getCachedElementList(final PreprocessorContext context,
                                       final String elementListId) {
    final NodeList result = findCachedElementList(context, elementListId);
    if (result == null) {
      throw context
          .makeException("Can't find any active element list for the '" + elementListId + "' id",
              null);
    }
    return result;
  }

  public int getElementListSize(final PreprocessorContext context, final String elementListId) {
    return getCachedElementList(context, elementListId).getLength();
  }


  public String findElementForIndex(final PreprocessorContext context, final String elementListId,
                                    final int elementIndex) {
    final String elementCacheId = makeElementId(elementListId, elementIndex);
    NodeContainer container = (NodeContainer) context.getSharedResource(elementCacheId);
    if (container == null) {
      container = (NodeContainer) context.getSharedResource(elementListId);

      if (container == null) {
        throw context
            .makeException("Can't find any active node list for the id '" + elementListId + '\'',
                null);
      }

      final NodeList list = container.getNodeList();
      if (elementIndex < 0 || elementIndex >= list.getLength()) {
        throw context
            .makeException("The Element Index is out of bounds [" + elementIndex + ']', null);
      }

      final Element element = (Element) list.item(elementIndex);

      if (element == null) {
        throw context.makeException("Wrong index [" + elementIndex + ']', null);
      }

      container = new NodeContainer(UID_COUNTER.getAndIncrement(), element);
      context.setSharedResource(elementCacheId, container);
    }
    return elementCacheId;
  }

}
