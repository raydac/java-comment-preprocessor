package com.igormaznitsa.jcpreprocessor.expression;

import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import com.igormaznitsa.jcpreprocessor.expression.operators.AbstractOperator;
import com.igormaznitsa.jcpreprocessor.expression.operators.OperatorSUB;
import java.util.List;

public class ExpressionTreeElement {

    private static final OperatorSUB OPERATOR_SUB = AbstractOperator.findForClass(OperatorSUB.class);
    private static final ExpressionTreeElement[] EMPTY = new ExpressionTreeElement[0];
    private ExpressionStackItem value;
    private ExpressionTreeElement[] elements;
    private ExpressionTreeElement parent;
    private int priority;
    private int position = 0;

    ExpressionTreeElement(final ExpressionStackItem value) {
        int arity = 0;
        if (value.getStackItemType() == ExpressionStackItemType.OPERATOR) {
            arity = ((AbstractOperator) value).getArity();
        } else if (value.getStackItemType() == ExpressionStackItemType.FUNCTION) {
            arity = ((AbstractFunction) value).getArity();
        }
        priority = value.getPriority().getPriority();
        this.value = value;
        elements = arity == 0 ? EMPTY : new ExpressionTreeElement[arity];
    }

    void makeMaxPriority() {
        priority = ExpressionStackItemPriority.VALUE.getPriority();
    }

    public ExpressionStackItem getItem() {
        return this.value;
    }

    public int getArity() {
        return elements.length;
    }

    public ExpressionTreeElement getParent() {
        return parent;
    }

    public int getPriority() {
        return priority;
    }

    public ExpressionTreeElement addSubTree(final ExpressionTree tree) {
        final ExpressionTreeElement root = tree.getRoot();
        if (root!=null){
            root.makeMaxPriority();
            addElementToNextFreeSlot(root);
        }
        return this;
    }

    public boolean replaceElement(final ExpressionTreeElement oldOne, final ExpressionTreeElement newOne) {
        boolean result = false;
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] == oldOne) {
                elements[i] = newOne;
                newOne.parent = this;
                result = true;
                break;
            }
        }
        return result;
    }

    public ExpressionTreeElement getElementAt(final int index) {
        return elements[index];
    }

    public ExpressionTreeElement addElement(final ExpressionTreeElement element) {
        final int newElementPriority = element.getPriority();

        ExpressionTreeElement result = this;

        final int currentPriority = getPriority();

        if (newElementPriority < currentPriority) {
            if (parent == null) {
                element.addElement(this);
                result = element;
            } else {
                result = parent.addElement(element);
            }
        } else if (newElementPriority == currentPriority) {
            if (parent != null) {
                parent.replaceElement(this, element);
            }
            element.elements[element.position] = this;
            element.position++;
            this.parent = element;
            result = element;
        } else {
            if (isFull()) {
                final int lastElementIndex = getArity() - 1;

                final ExpressionTreeElement lastElement = elements[lastElementIndex];
                if (lastElement.getPriority() > newElementPriority) {
                    element.addElementToNextFreeSlot(lastElement);
                    elements[lastElementIndex] = element;
                    element.parent = this;
                    result = element;
                }

            } else {
                addElementToNextFreeSlot(element);
                result = element;
            }
        }
        return result;
    }

    public boolean isFull() {
        return position == elements.length;
    }

    public void fillArguments(final List<ExpressionTree> arguments) {
        if (arguments == null) {
            throw new NullPointerException("Argument list is null");
        }

        if (elements.length != arguments.size()) {
            throw new IllegalArgumentException("Wrong argument list size");
        }

        int i = 0;
        for (ExpressionTree arg : arguments) {
            if (arg == null) {
                throw new NullPointerException("Argument [" + (i + 1) + "] is null");
            }

            if (elements[i] != null) {
                throw new IllegalStateException("Non-null slot detected, it is possible that there is a program error, contact a developer please");
            }

            final ExpressionTreeElement root = arg.getRoot();
            if (root == null) {
                throw new IllegalArgumentException("Empty argument [" + (i + 1) + "] detected");
            }
            elements[i] = root;
            root.parent = this;

            i++;
        }
    }

    private void addElementToNextFreeSlot(final ExpressionTreeElement element) {
        if (elements.length == 0) {
            throw new IllegalArgumentException("The element doesn't support arguments [" + value.toString() + ']');
        } else {
            if (isFull()) {
                throw new IllegalStateException("There is not any possibility to add new argument [" + value.toString() + ']');
            } else {
                elements[position++] = element;
            }
        }
        element.parent = this;
    }

    public void postProcess() {
        switch (value.getStackItemType()) {
            case OPERATOR: {
                if (value == OPERATOR_SUB) {
                    if (elements[0] != null && elements[1] == null) {
                        final ExpressionTreeElement left = elements[0];
                        final ExpressionStackItem item = left.getItem();
                        if (item.getStackItemType() == ExpressionStackItemType.VALUE) {
                            final Value val = (Value) item;
                            if (val.getType() == ValueType.INT) {
                                elements = EMPTY;
                                value = Value.valueOf(Long.valueOf(0 - val.asLong().longValue()));
                                makeMaxPriority();
                            } else if (val.getType() == ValueType.FLOAT) {
                                elements = EMPTY;
                                value = Value.valueOf(Float.valueOf(0 - val.asFloat().floatValue()));
                                makeMaxPriority();
                            } else {
                                left.postProcess();
                            }
                        }
                    } else {
                        for (final ExpressionTreeElement element : elements) {
                            if (element != null) {
                                element.postProcess();
                            }
                        }
                    }
                } else {
                    for (final ExpressionTreeElement element : elements) {
                        if (element != null) {
                            element.postProcess();
                        }
                    }
                }
            }
            break;
            case FUNCTION: {
                for (final ExpressionTreeElement element : elements) {
                    if (element != null) {
                        element.postProcess();
                    }
                }
            }
            break;
        }
    }
}
