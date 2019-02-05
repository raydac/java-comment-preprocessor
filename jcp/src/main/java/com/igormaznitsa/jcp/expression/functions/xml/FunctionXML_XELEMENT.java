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
import com.igormaznitsa.meta.annotation.MustNotContainNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

/**
 * The class implements the xml_xpathelement function handler
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionXML_XELEMENT extends AbstractXMLFunction {

  private static final ValueType[][] ARG_TYPES = new ValueType[][] {{ValueType.STRING, ValueType.STRING}};

  @Override
  @Nonnull
  public String getName() {
    return "xml_xelement";
  }

  @Nonnull
  public Value executeStrStr(@Nonnull final PreprocessorContext context, @Nonnull final Value documentId, @Nonnull final Value xPath) {
    final String documentIdStr = documentId.asString();
    final String pathStr = xPath.asString();

    final String xpathElementId = makeXPathElementId(documentIdStr, pathStr);
    final Document document = getCachedDocument(context, documentIdStr);

    Element elem = findCachedElement(context, xpathElementId);
    if (elem == null) {
      try {
        final XPathExpression expression = prepareXPathExpression(pathStr);
        elem = (Element) expression.evaluate(document, XPathConstants.NODE);
        if (elem == null) {
          throw context.makeException("Can't find element for xpath [" + pathStr + ']', null);
        }
      } catch (XPathExpressionException ex) {
        throw context.makeException("Error during XPath compilation [" + pathStr + ']', ex);
      } catch (ClassCastException ex) {
        throw context.makeException("Can't get element for XPath [" + pathStr + ']', ex);
      }
      final NodeContainer container = new NodeContainer(UID_COUNTER.getAndIncrement(), elem);
      context.setSharedResource(xpathElementId, container);
    }
    return Value.valueOf(xpathElementId);
  }

  @Override
  public int getArity() {
    return 2;
  }

  @Override
  @Nonnull
  @MustNotContainNull
  public ValueType[][] getAllowedArgumentTypes() {
    return ARG_TYPES;
  }

  @Override
  @Nonnull
  public String getReference() {
    return "find single element with XPath, error if not found";
  }

  @Override
  @Nonnull
  public ValueType getResultType() {
    return ValueType.STRING;
  }
}
