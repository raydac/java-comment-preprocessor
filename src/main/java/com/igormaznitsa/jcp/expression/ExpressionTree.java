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
     * @return true if the tree is empty one else false
     */
    public boolean isEmpty() {
        return last == null;
    }
    
    /**
     * Add new expression item into tree
     * @param item an item to be added, must not be null
     */
    public void addItem(final ExpressionItem item) {
        if (item == null){
            throw new NullPointerException("Item is null");
        }
        if (last == null) {
            last = new ExpressionTreeElement(item);
        } else {
            last = last.addTreeElement(new ExpressionTreeElement(item));
        }
    }

    /**
     * Add whole tree as a tree element, also it sets the maximum priority to the new element 
     * @param tree a tree to be added as an item, must not be null
     */
    public void addTree(final ExpressionTree tree) {
        if (tree == null) {
            throw new NullPointerException("Tree is null");
        }
        if (last == null){
            final ExpressionTreeElement thatTreeRoot = tree.getRoot();
            if (thatTreeRoot!=null){
                last = thatTreeRoot;
                last.makeMaxPriority();
            }
        } else {
            last = last.addSubTree(tree);
        }
    }

    /**
     * Get the root of the tree
     * @return the root of the tree or null if the tree is empty
     */
    public ExpressionTreeElement getRoot() {
        if (last == null) {
            return null;
        } else {
            ExpressionTreeElement element = last;
            while(true){
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
        if (root != null){
            root.postProcess();
        }
    }
    
}
