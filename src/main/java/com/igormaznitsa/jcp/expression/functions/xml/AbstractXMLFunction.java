/*
 * Copyright 2014 Igor Maznitsa.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.functions.AbstractFunction;

import org.apache.xpath.jaxp.XPathFactoryImpl;
import org.w3c.dom.*;

public abstract class AbstractXMLFunction extends AbstractFunction {

  @Nonnull
  public static String makeElementListId(@Nonnull final Element parentName, @Nonnull final String elementName) {
    return buildPathForElement(parentName) + "_#list_" + elementName;
  }

  @Nonnull
  public static String makeDocumentId(@Nonnull final String fileName) {
    return "xmlDocument_" + fileName;
  }

  @Nonnull
  public static String makeDocumentRootId(@Nonnull final String documentId) {
    return documentId + "_#root";
  }

  @Nonnull
  public static String makeElementId(@Nonnull final String elementListId, @Nonnull final int elementIndex) {
    return elementListId + '_' + elementIndex;
  }

  @Nonnull
  public static String makeXPathListId(@Nonnull final String documentId, @Nonnull final String xpath) {
    return documentId + "_#xpath_" + xpath;
  }

  @Nonnull
  public static String makeXPathElementId(@Nonnull final String documentId, @Nonnull final String xpath) {
    return documentId + "_#xpathelement_" + xpath;
  }

  @Nonnull
  public String getAttribute(@Nonnull final PreprocessorContext context, @Nonnull final String elementId, @Nonnull final String attributeName) {
    final NodeContainer container = (NodeContainer) context.getSharedResource(elementId);
    if (container == null) {
      throw context.makeException("Can't find any active element with the \'" + elementId + "\' id", null);
    }
    try {
      return ((Element) container.getNode()).getAttribute(attributeName);
    } catch (ClassCastException ex) {
      throw context.makeException("Incompatible cached element type [" + elementId + '.' + attributeName + ']', ex);
    }
  }

  @Nonnull
  public Document getCachedDocument(@Nonnull final PreprocessorContext context, @Nonnull final String documentId) {
    final NodeContainer container = (NodeContainer) context.getSharedResource(documentId);
    if (container == null) {
      throw context.makeException("Can't find any document for the \'" + documentId + "\' id", null);
    }

    try {
      return (Document) container.getNode();
    } catch (ClassCastException ex) {
      throw context.makeException("Incompatible cached element type [" + documentId + ']', ex);
    }
  }

  @Nullable
  public Element findCachedElement(@Nonnull final PreprocessorContext context, @Nonnull final String elementId) {
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

  @Nonnull
  public Element getCachedElement(@Nonnull final PreprocessorContext context, @Nonnull final String elementId) {
    final Element element = findCachedElement(context, elementId);
    if (element == null) {
      throw context.makeException("Can't find any active element for the \'" + elementId + "\' id", null);
    }
    return element;
  }

  @Nullable
  public NodeList findCachedElementList(@Nonnull final PreprocessorContext context, @Nonnull final String elementListId) {
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

  @Nonnull
  public NodeList getCachedElementList(@Nonnull final PreprocessorContext context, @Nonnull final String elementListId) {
    final NodeList result = findCachedElementList(context, elementListId);
    if (result == null) {
      throw context.makeException("Can't find any active element list for the \'" + elementListId + "\' id", null);
    }
    return result;
  }

  public int getElementListSize(@Nonnull final PreprocessorContext context, @Nonnull final String elementListId) {
    return getCachedElementList(context, elementListId).getLength();
  }

  @Nonnull
  public static String buildPathForElement(@Nonnull final Element element) {
    final StringBuilder result = new StringBuilder();

    Node thenode = element;

    while (thenode != null) {
      int level = 0;
      Node sibling = thenode;
      while (sibling != null) {
        level++;
        sibling = sibling.getPreviousSibling();
      }

      result.append('/').append(thenode.getNodeName()).append('{').append(level).append('}');
      thenode = thenode.getParentNode();
    }

    return result.toString();
  }

  @Nonnull
  public static String getFirstLevelTextContent(@Nonnull final Node node) {
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

  @Nonnull
  public String findElementForIndex(@Nonnull final PreprocessorContext context, @Nonnull final String elementListId, final int elementIndex) {
    final String elementCacheId = makeElementId(elementListId, elementIndex);
    NodeContainer container = (NodeContainer) context.getSharedResource(elementCacheId);
    if (container == null) {
      container = (NodeContainer) context.getSharedResource(elementListId);

      if (container == null) {
        throw context.makeException("Can't find any active node list for the id \'" + elementListId + '\'', null);
      }

      final NodeList list = container.getNodeList();
      if (elementIndex < 0 || elementIndex >= list.getLength()) {
        throw context.makeException("The Element Index is out of bounds [" + elementIndex + ']', null);
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

  @Nonnull
  protected static XPathExpression prepareXPathExpression(@Nonnull final String path) throws XPathExpressionException {
    final XPathFactory factory = new XPathFactoryImpl();
    final XPath xpath = factory.newXPath();
    return xpath.compile(path);
  }

}
