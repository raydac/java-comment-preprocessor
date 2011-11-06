package com.igormaznitsa.jcpreprocessor.expression.functions.xml;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import org.w3c.dom.Element;

public final class FunctionXML_GETELEMENTTEXT extends AbstractFunction {

    private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING}};
    
    @Override
    public String getName() {
        return "xml_getelementtext";
    }

    public Value executeStr(final PreprocessorContext context, final Value elementid) {
        final String elementIdStr = elementid.asString();
        
        final NodeContainer container = (NodeContainer) context.getSharedResource(elementIdStr);
        if (container == null) {
            throw new RuntimeException("Can't find opened xml element for the id \'"+elementIdStr+'\'');
        }
    
        final Element element = (Element)container.getNode();
        return Value.valueOf(element.getTextContent());
    }

    @Override
    public int getArity() {
        return 1 ;
    }

    @Override
    public ValueType[][] getAllowedArgumentTypes() {
        return ARG_TYPES;
    }

    @Override
    public String getReference() {
        return "it returns text from an element including text of all its children";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.STRING;
    }
    
      
}
