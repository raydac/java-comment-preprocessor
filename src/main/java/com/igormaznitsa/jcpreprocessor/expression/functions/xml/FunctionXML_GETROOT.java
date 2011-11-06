package com.igormaznitsa.jcpreprocessor.expression.functions.xml;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
                throw new RuntimeException("Can't find an xml document for the id \'" + docIdStr + '\'');
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
        return "it returns the root element of a xml document";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.STRING;
    }
}
