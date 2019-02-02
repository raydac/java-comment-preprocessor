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

package com.igormaznitsa.jcp.expression;

import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.meta.annotation.MustNotContainNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

/**
 * The class describes an object contains an expression tree
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class ExpressionTree {

  private final FilePositionInfo[] includeStack;
  private final String sources;
  private ExpressionTreeElement last = ExpressionTreeElement.EMPTY_SLOT;

  public ExpressionTree() {
    this(null, null);
  }

  public ExpressionTree(@Nullable @MustNotContainNull final FilePositionInfo[] callStack, @Nullable final String sources) {
    this.includeStack = callStack == null ? PreprocessingState.EMPTY_STACK : callStack;
    this.sources = sources == null ? "" : sources;
  }

  /**
   * Allows to check that the tree is empty
   *
   * @return true if the tree is empty one else false
   */
  public boolean isEmpty() {
    return last.isEmptySlot();
  }

  /**
   * Add new expression item into tree
   *
   * @param item an item to be added, must not be null
   */
  public void addItem(@Nonnull final ExpressionItem item) {
    if (item == null) {
      throw new PreprocessorException("[Expression]Item is null", this.sources, this.includeStack, null);
    }

    if (last.isEmptySlot()) {
      last = new ExpressionTreeElement(item, this.includeStack, this.sources);
    } else {
      last = last.addTreeElement(new ExpressionTreeElement(item, this.includeStack, this.sources));
    }
  }

  /**
   * Add whole tree as a tree element, also it sets the maximum priority to the new element
   *
   * @param tree a tree to be added as an item, must not be null
   */
  public void addTree(@Nonnull final ExpressionTree tree) {
    assertNotNull("Tree is null", tree);
    if (last.isEmptySlot()) {
      final ExpressionTreeElement thatTreeRoot = tree.getRoot();
      if (!thatTreeRoot.isEmptySlot()) {
        last = thatTreeRoot;
        last.makeMaxPriority();
      }
    } else {
      last = last.addSubTree(tree);
    }
  }

  /**
   * Get the root of the tree
   *
   * @return the root of the tree or EMPTY_SLOT if the tree is empty
   */
  @Nonnull
  public ExpressionTreeElement getRoot() {
    if (last.isEmptySlot()) {
      return this.last;
    } else {
      ExpressionTreeElement element = last;
      while (true) {
        final ExpressionTreeElement next = element.getParent();
        if (next == null) {
          return element;
        } else {
          element = next;
        }
      }
    }
  }

  /**
   * It can be called after the tree has been formed to optimize inside structures
   */
  public void postProcess() {
    final ExpressionTreeElement root = getRoot();
    if (!root.isEmptySlot()) {
      root.postProcess();
    }
  }

}
