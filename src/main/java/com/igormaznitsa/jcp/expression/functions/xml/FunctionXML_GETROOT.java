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

        NodeContainer nodeContainer = (NodeContainer) context.getSharedResource(docIdStrRoot);
        if (nodeContainer == null) {
            nodeContainer = (NodeContainer) context.getSharedResource(docIdStr);
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
