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

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.functions.AbstractFunction;
import org.w3c.dom.*;

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

  public String getAttribute(final PreprocessorContext context, final String elementId, final String attributeName) {
    final NodeContainer container = (NodeContainer) context.getSharedResource(elementId);
    if (container == null) {
      final String text = "Can't find any active element with the \'" + elementId + "\' id";
      throw new IllegalArgumentException(text, context.makeException(text, null));
    }
    try {
      return ((Element) container.getNode()).getAttribute(attributeName);
    }
    catch (ClassCastException ex) {
      final String text = "Incompatible cached element type [" + elementId + '.' + attributeName + ']';
      throw new IllegalArgumentException(text, context.makeException(text, ex));
    }
  }

  public Document getCachedDocument(final PreprocessorContext context, final String documentId) {
    final NodeContainer container = (NodeContainer) context.getSharedResource(documentId);
    if (container == null) {
      final String text = "Can't find any document for the \'" + documentId + "\' id";
      throw new IllegalArgumentException(text, context.makeException(text, null));
    }

    try {
      return (Document) container.getNode();
    }
    catch (ClassCastException ex) {
      final String text = "Incompatible cached element type [" + documentId + ']';
      throw new IllegalArgumentException(text, context.makeException(text, ex));
    }
  }

  public Element findCachedElement(final PreprocessorContext context, final String elementId) {
    final NodeContainer container = (NodeContainer) context.getSharedResource(elementId);
    if (container == null) {
      return null;
    }

    try {
      return (Element) container.getNode();
    }
    catch (ClassCastException ex) {
      final String text = "Incompatible cached element type [" + elementId + ']';
      throw new IllegalArgumentException(text, context.makeException(text, ex));
    }
  }

  public Element getCachedElement(final PreprocessorContext context, final String elementId) {
    final Element element = findCachedElement(context, elementId);
    if (element == null) {
      final String text = "Can't find any active element for the \'" + elementId + "\' id";
      throw new IllegalArgumentException(text, context.makeException(text, null));
    }
    return element;
  }

  public NodeList findCachedElementList(final PreprocessorContext context, final String elementListId) {
    final NodeContainer container = (NodeContainer) context.getSharedResource(elementListId);
    if (container == null) {
      return null;
    }
    try {
      return container.getNodeList();
    }
    catch (ClassCastException ex) {
      final String text = "Incompatible cached element type [" + elementListId + ']';
      throw new IllegalArgumentException(text, context.makeException(text, ex));
    }
  }

  public NodeList getCachedElementList(final PreprocessorContext context, final String elementListId) {
    final NodeList result = findCachedElementList(context, elementListId);
    if (result == null) {
      final String text = "Can't find any active element list for the \'" + elementListId + "\' id";
      throw new IllegalArgumentException(text, context.makeException(text, null));
    }
    return result;
  }

  public int getElementListSize(final PreprocessorContext context, final String elementListId) {
    return getCachedElementList(context, elementListId).getLength();
  }

  public static String buildPathForElement(final Element element) {
    final StringBuilder result = new StringBuilder(element.getNodeName());

    Node thenode = element.getParentNode();

    while (thenode != null) {
      result.append('/').append(thenode.getNodeName());
      thenode = thenode.getParentNode();
    }

    return result.toString();
  }

  public String findElementForIndex(final PreprocessorContext context, final String elementListId, final int elementIndex) {
    final String elementCacheId = makeElementId(elementListId, elementIndex);
    NodeContainer container = (NodeContainer) context.getSharedResource(elementCacheId);
    if (container == null) {
      container = (NodeContainer) context.getSharedResource(elementListId);

      if (container == null) {
        final String text = "Can't find any active node list for the id \'" + elementListId + '\'';
        throw new IllegalArgumentException(text, context.makeException(text, null));
      }

      final NodeList list = container.getNodeList();
      if (elementIndex < 0 || elementIndex >= list.getLength()) {
        final String text = "The Element Index is out of bound [" + elementIndex + ']';
        throw new IllegalArgumentException(text, context.makeException(text, null));
      }

      final Element element = (Element) list.item(elementIndex);

      if (element == null) {
        final String text = "Index is not valud [" + elementIndex + ']';
        throw new IllegalArgumentException(text);
      }

      container = new NodeContainer(UID_COUNTER.getAndIncrement(), element);
      context.setSharedResource(elementCacheId, container);
    }
    return elementCacheId;
  }
}
