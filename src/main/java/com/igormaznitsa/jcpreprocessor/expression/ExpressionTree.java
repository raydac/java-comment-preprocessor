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
package com.igormaznitsa.jcpreprocessor.expression;

public class ExpressionTree {
    
    private ExpressionTreeElement last;
    
    public boolean isEmpty() {
        return last == null;
    }
    
    public void addItem(final ExpressionStackItem item) {
        if (last == null) {
            last = new ExpressionTreeElement(item);
        } else {
            last = last.addElement(new ExpressionTreeElement(item));
        }
    }

    public void addTree(final ExpressionTree tree) {
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

    public void postProcess() {
        final ExpressionTreeElement root = getRoot();
        if (root != null){
            root.postProcess();
        }
    }
    
}
