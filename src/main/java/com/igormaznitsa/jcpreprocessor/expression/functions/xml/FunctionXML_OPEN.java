package com.igormaznitsa.jcpreprocessor.expression.functions.xml;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
                throw new RuntimeException("Can't get source file \'" + name + '\'', unexpected);
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

        try {
            return docBuilderFactory.newDocumentBuilder().parse(file);
        } catch (ParserConfigurationException unexpected) {
            throw new RuntimeException("XML parser configuration exception", unexpected);
        } catch (SAXException unexpected) {
            throw new RuntimeException("Exception during XML parsing", unexpected);
        } catch (IOException unexpected) {
            throw new RuntimeException("Can't read file", unexpected);
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
