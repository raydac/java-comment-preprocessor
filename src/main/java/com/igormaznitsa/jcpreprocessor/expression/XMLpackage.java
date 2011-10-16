package com.igormaznitsa.jcpreprocessor.expression;


import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.util.Vector;
import java.io.IOException;

import org.w3c.dom.*;

public class XMLpackage
{
    //xml_open+
    //xml_getDocumentElement+
    //xml_getElemenName+
    //xml_getElementsForName+
    //xml_elementsNumber+
    //xml_elementAt+
    //xml_getAttribute+

    private static Vector p_openedXMLdocuments;
    private static Vector p_elementsVector;
    private static Vector p_nodeListVector;

    static
    {
        init();
    }

    private static final int getElementIndex(Element _element) throws IOException
    {
        if (_element==null) throw new IOException("Null element has been founded");
        int i_index =  p_elementsVector.indexOf(_element);
        if (i_index<0)
        {
            i_index = p_elementsVector.size();
            p_elementsVector.addElement(_element);
        }
        return i_index;
    }

    private static final int getNodeListIndex(NodeList _nodeList)
    {
        int i_index =  p_nodeListVector.indexOf(_nodeList);
        if (i_index<0)
        {
            i_index = p_nodeListVector.size();
            p_nodeListVector.addElement(_nodeList);
        }
        return i_index;
    }

    public static final void init()
    {
        p_openedXMLdocuments = new Vector(8);
        p_elementsVector = new Vector(8);
        p_nodeListVector = new Vector(8);
    }

    public static final void release()
    {
        p_openedXMLdocuments = null;
        p_elementsVector = null;
        p_nodeListVector = null;
    }


    // ��������� ������� xml_getAttribute
    public static final void processXML_GETATTRIBUTE(Expression _stack, int _index) throws IOException
    {
     }
}
