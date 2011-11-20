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
import org.w3c.dom.Element;

/**
 * The class implements the xml_getattribute function
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class FunctionXML_GETATTRIBUTE extends AbstractFunction {

    private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING, ValueType.STRING}};
    
    @Override
    public String getName() {
        return "xml_getattribute";
    }

    public Value executeStrStr(final PreprocessorContext context, final Value elementId, final Value attributeName) {
        final String elementIdStr = elementId.asString();
        final String attributeNameStr = attributeName.asString();
        
        final NodeContainer container = (NodeContainer)context.getSharedResource(elementIdStr);
        if (container == null){
            throw new IllegalArgumentException("Can't find any active element for the \'"+elementIdStr+"\' id");
        }
        
        return Value.valueOf(((Element)container.getNode()).getAttribute(attributeNameStr));
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
        return "it returns an attribute value of an element";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.STRING;
    }
    
  
}
