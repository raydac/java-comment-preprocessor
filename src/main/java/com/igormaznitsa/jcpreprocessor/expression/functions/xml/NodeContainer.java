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
