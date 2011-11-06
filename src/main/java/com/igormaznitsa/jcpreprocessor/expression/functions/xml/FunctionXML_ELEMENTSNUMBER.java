package com.igormaznitsa.jcpreprocessor.expression.functions.xml;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import org.w3c.dom.NodeList;

public final class FunctionXML_ELEMENTSNUMBER extends AbstractFunction {

    private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING}};
    
    @Override
    public String getName() {
        return "xml_elementsnumber";
    }

    public Value executeStr(final PreprocessorContext context, final Value listId) {
        final String elementIdStr = listId.asString();
        
        final NodeContainer container = (NodeContainer) context.getSharedResource(elementIdStr);
        if (container == null) {
            throw new RuntimeException("Can't find any active element for the id  \'"+elementIdStr+'\'');
        }
        
        final NodeList list = (NodeList)container.getNodeList();
        
        return Value.valueOf(Long.valueOf(list.getLength()));
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
        return "it returns the length of a element list";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.INT;
    }
    
   
}
