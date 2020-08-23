/*
 * Copyright 2002-2019 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.igormaznitsa.jcp.expression.functions.xml;

import java.util.Objects;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * It's a special auxiliary class to save XML node data in a preprocessor
 * storage
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznnitsa.com)
 */
public class NodeContainer {

  private final Node node;
  private final NodeList nodeList;
  private final long id;

  public NodeContainer(final long id, final Node node) {
    Objects.requireNonNull(node, "Node is null");
    this.id = id;
    this.node = node;
    this.nodeList = null;
  }

  public NodeContainer(final long id, final NodeList list) {
    Objects.requireNonNull(list, "NodeList is null");
    this.id = id;
    this.node = null;
    this.nodeList = list;
  }


  public NodeList getNodeList() {
    return this.nodeList;
  }


  public Node getNode() {
    return this.node;
  }

  public long getId() {
    return this.id;
  }
}
