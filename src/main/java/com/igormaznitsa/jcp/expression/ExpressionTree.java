/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.jcp.expression;

/**
 * The class describes an object contains an expression tree
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class ExpressionTree {

  private ExpressionTreeElement last;

  /**
   * Allows to check that the tree is empty
   *
   * @return true if the tree is empty one else false
   */
  public boolean isEmpty() {
    return last == null;
  }

  /**
   * Add new expression item into tree
   *
   * @param item an item to be added, must not be null
   */
  public void addItem(final ExpressionItem item) {
    if (item == null) {
      throw new NullPointerException("Item is null");
    }
    if (last == null) {
      last = new ExpressionTreeElement(item);
    }
    else {
      last = last.addTreeElement(new ExpressionTreeElement(item));
    }
  }

  /**
   * Add whole tree as a tree element, also it sets the maximum priority to the
   * new element
   *
   * @param tree a tree to be added as an item, must not be null
   */
  public void addTree(final ExpressionTree tree) {
    if (tree == null) {
      throw new NullPointerException("Tree is null");
    }
    if (last == null) {
      final ExpressionTreeElement thatTreeRoot = tree.getRoot();
      if (thatTreeRoot != null) {
        last = thatTreeRoot;
        last.makeMaxPriority();
      }
    }
    else {
      last = last.addSubTree(tree);
    }
  }

  /**
   * Get the root of the tree
   *
   * @return the root of the tree or null if the tree is empty
   */
  public ExpressionTreeElement getRoot() {
    if (last == null) {
      return null;
    }
    else {
      ExpressionTreeElement element = last;
      while (true) {
        final ExpressionTreeElement next = element.getParent();
        if (next == null) {
          return element;
        }
        else {
          element = next;
        }
      }
    }
  }

  /**
   * It can be called after the tree has been formed to optimize inside
   * structures
   */
  public void postProcess() {
    final ExpressionTreeElement root = getRoot();
    if (root != null) {
      root.postProcess();
    }
  }

}
