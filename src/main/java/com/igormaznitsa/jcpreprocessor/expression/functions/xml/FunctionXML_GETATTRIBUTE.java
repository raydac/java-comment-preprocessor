package com.igormaznitsa.jcpreprocessor.expression.functions.xml;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import org.w3c.dom.Element;

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
            throw new RuntimeException("Can't find any active element for the id \'"+elementIdStr+'\'');
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
