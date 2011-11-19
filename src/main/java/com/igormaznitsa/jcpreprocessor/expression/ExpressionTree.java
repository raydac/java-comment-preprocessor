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
