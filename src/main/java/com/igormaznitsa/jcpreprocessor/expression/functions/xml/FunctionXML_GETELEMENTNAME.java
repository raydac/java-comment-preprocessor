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
package com.igormaznitsa.jcpreprocessor.expression.functions.xml;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;

public final class FunctionXML_GETELEMENTNAME extends AbstractFunction {

    private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING}};
    
    @Override
    public String getName() {
        return "xml_getelementname";
    }

    public Value executeStr(final PreprocessorContext context, final Value elementId) {
        final String elementIdStr = elementId.asString();
        
        final NodeContainer container = (NodeContainer) context.getSharedResource(elementIdStr);
        if (container == null) {
            throw new RuntimeException("Can't find any active element for the id \'"+elementIdStr+'\'');
        }
        
        return Value.valueOf(container.getNode().getNodeName());
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
        return "it returns the node name of the element";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.STRING;
    }
    
    
}
