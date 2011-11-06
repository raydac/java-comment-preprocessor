package com.igormaznitsa.jcpreprocessor.expression.functions.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeContainer {
    private final Node node;
    private final NodeList nodeList;
    private final long id;
    
    public NodeContainer(final long id, final Node node) {
        if (node == null) {
            throw new NullPointerException("Node is null");
        }
        this.id = id;
        this.node = node;
        this.nodeList = null;
    }
    
    public NodeContainer(final long id, final NodeList list) {
        if (list == null) {
            throw new NullPointerException("NodeList is null");
        }
        this.id = id;
        this.node = null;
        this.nodeList = list;
    }
    
    public NodeList getNodeList(){
        return this.nodeList;
    }
    
    public Node getNode() {
        return this.node;
    }
    
    public long getId(){
        return this.id;
    }
}
