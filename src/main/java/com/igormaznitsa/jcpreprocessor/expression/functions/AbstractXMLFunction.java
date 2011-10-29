package com.igormaznitsa.jcpreprocessor.expression.functions;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.*;

public abstract class AbstractXMLFunction extends AbstractFunction {

    private static List<Element> xmlElements = new ArrayList<Element>();
    private static List<NodeList> xmlNodeLists = new ArrayList<NodeList>();
    private static List<Document> xmlDocuments = new ArrayList<Document>();

    protected static Document getXmlDocumentForIndex(final int index) {
        if (index < 0 || index >= xmlDocuments.size()) {
            throw new IllegalArgumentException("The XML document index is out of bound ["+index+']');
        }
        return xmlDocuments.get(index);
    }

    protected static int addXmlDocument(final Document element) {
        final int index = xmlDocuments.size();
        xmlDocuments.add(index, element);
        return index;
    }

    protected static int findXmlDocumentIndex(final Document element) {
        int index = xmlDocuments.indexOf(element);
        if (index < 0) {
            index = addXmlDocument(element);
        }
        return index;
    }

    protected static Element getXmlElementForIndex(final int index) {
        if (index < 0 || index >= xmlElements.size()) {
            throw new IllegalArgumentException("The XML element index is out of bound");
        }
        return xmlElements.get(index);
    }

    protected static int addXmlElement(final Element element) {
        if (element == null) {
            throw new NullPointerException("Element is null");
        }
        final int index = xmlElements.size();
        xmlElements.add(index, element);
        return index;
    }

    protected static int findXmlElementIndex(final Element element) {
        int index = xmlElements.indexOf(element);
        if (index < 0) {
            index = addXmlElement(element);
        }
        return index;
    }

    protected static NodeList getXmlNodeListIndex(final int index) {
        if (index < 0 || index >= xmlNodeLists.size()) {
            throw new IllegalArgumentException("The XML node list index is out of bound");
        }
        return xmlNodeLists.get(index);
    }

    protected static int addXmlNodeList(final NodeList element) {
        final int index = xmlNodeLists.size();
        xmlNodeLists.add(index, element);
        return index;
    }

    protected static int findXmlNodelistIndex(final NodeList element) {
        int index = xmlNodeLists.indexOf(element);
        if (index < 0) {
            index = addXmlNodeList(element);
        }
        return index;
    }
}
