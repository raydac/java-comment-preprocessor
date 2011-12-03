/*
 * Copyright 2011 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307  USA
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
public final class FunctionXML_OPEN extends AbstractFunction {

    public static final String RES_XML_DOC_PREFIX = "xml_doc_";
    public static final String RES_XML_ELEMENT_PREFIX = "xml_elem_";
    
    private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING}};

    @Override
    public String getName() {
        return "xml_open";
    }

    public Value executeStr(final PreprocessorContext context, final Value fileName) {
        final String name = fileName.asString();

        final String xml_docId = RES_XML_DOC_PREFIX + name;
        NodeContainer docContainer = (NodeContainer) context.getSharedResource(xml_docId);

        if (docContainer == null) {
            File file = null;
            try {
                file = context.getSourceFile(name);
            } catch (IOException unexpected) {
                throw new IllegalArgumentException("Can't find source file \'" + name + '\'', unexpected);
            }

            final Document document = openFileAndParse(file);
            docContainer = new NodeContainer(UID_COUNTER.getAndIncrement(), document);
            context.setSharedResource(xml_docId, docContainer);
        }

        return Value.valueOf(xml_docId);
    }

    private Document openFileAndParse(final File file) {
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringComments(true);
        docBuilderFactory.setCoalescing(true);
        docBuilderFactory.setValidating(false);

        try {
            return docBuilderFactory.newDocumentBuilder().parse(file);
        } catch (ParserConfigurationException unexpected) {
            throw new IllegalStateException("XML parser configuration exception", unexpected);
        } catch (SAXException unexpected) {
            throw new IllegalStateException("Exception during XML parsing", unexpected);
        } catch (IOException unexpected) {
            throw new IllegalArgumentException("Can't read XML file", unexpected);
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
        return "it opens an XML file and return its descriptor";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.STRING;
    }
}
