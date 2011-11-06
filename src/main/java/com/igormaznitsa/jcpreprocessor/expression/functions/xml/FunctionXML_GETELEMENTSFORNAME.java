package com.igormaznitsa.jcpreprocessor.expression.functions.xml;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class FunctionXML_GETELEMENTSFORNAME extends AbstractFunction {

    private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING,ValueType.STRING}};
    
    @Override
    public String getName() {
        return "xml_getelementsforname";
    }

    public Value executeStrStr(final PreprocessorContext context, final Value elementId, final Value name) {
        final String elementIdStr = elementId.asString();
        final String nameStr = name.asString();

        final String nodeListId = elementIdStr + "_nodelist_" + name;
        final Value result = Value.valueOf(nodeListId);


        NodeContainer container = (NodeContainer) context.getSharedResource(nodeListId);
        if (container == null) {
            container = (NodeContainer) context.getSharedResource(elementIdStr);
            if (container == null) {
                throw new RuntimeException("Can't find any element for the id \'" + elementIdStr + '\'');
            }
            final Element element = (Element) container.getNode();
            final NodeList list = element.getElementsByTagName(nameStr);
            container = new NodeContainer(UID_COUNTER.getAndIncrement(), list);
            context.setSharedResource(nodeListId, container);
        }

        return result;
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
        return "it finds children for a name in the element and return the list id";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.STRING;
    }
}
