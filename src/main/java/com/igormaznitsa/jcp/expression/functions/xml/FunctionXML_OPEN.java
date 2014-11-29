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
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The class implements the xml_open function handler
 *
 * @author Igor Maznits (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionXML_OPEN extends AbstractXMLFunction {

  public static final String RES_XML_DOC_PREFIX = "xml_doc_";
  public static final String RES_XML_ELEMENT_PREFIX = "xml_elem_";

  private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING}};

  @Override
  public String getName() {
    return "xml_open";
  }

  public Value executeStr(final PreprocessorContext context, final Value filePath) {
    final String name = filePath.asString();

    final String documentId = makeDocumentId(name);
    final String documentIdRoot = makeDocumentRootId(documentId);
    
    NodeContainer docContainer = (NodeContainer) context.getSharedResource(documentId);
    if (docContainer == null) {
      File file = null;
      try {
        file = context.getSourceFile(name);
      }
      catch (IOException unexpected) {
        final String text = "Can't read \'" + name + '\'';
        throw new IllegalArgumentException(text, context.makeException(text, unexpected));
      }

      final Document document = openFileAndParse(context, file);
      docContainer = new NodeContainer(UID_COUNTER.getAndIncrement(), document);
      context.setSharedResource(documentId, docContainer);
      final NodeContainer rootContainer = new NodeContainer(UID_COUNTER.getAndIncrement(), document.getDocumentElement());
      context.setSharedResource(documentIdRoot, rootContainer);
    }

    return Value.valueOf(documentId);
  }

  private Document openFileAndParse(final PreprocessorContext context, final File file) {
    final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    docBuilderFactory.setIgnoringComments(true);
    docBuilderFactory.setCoalescing(true);
    docBuilderFactory.setValidating(false);

    try {
      return docBuilderFactory.newDocumentBuilder().parse(file);
    }
    catch (ParserConfigurationException unexpected) {
      final String text = "XML parser configuration exception";
      throw new IllegalStateException(text,context.makeException(text, unexpected));
    }
    catch (SAXException unexpected) {
      final String text = "Exception during XML parsing";
      throw new IllegalStateException(text, context.makeException(text, unexpected));
    }
    catch (IOException unexpected) {
      final String text = "Can't read XML file";
      throw new IllegalArgumentException(text, context.makeException(text, unexpected));
    }
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
    return "open and parse into DOM an xml file and return its descriptor";
  }

  @Override
  public ValueType getResultType() {
    return ValueType.STRING;
  }
}
