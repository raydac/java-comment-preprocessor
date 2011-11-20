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

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.functions.AbstractFunction;
import com.igormaznitsa.jcpreprocessor.expression.functions.FunctionDefinedByUser;
import com.igormaznitsa.jcpreprocessor.expression.operators.AbstractOperator;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Expression {

    private static final Class[] OPERATOR_SIGNATURE_1 = new Class[]{Value.class};
    private static final Class[] OPERATOR_SIGNATURE_2 = new Class[]{Value.class, Value.class};
    private final PreprocessorContext context;
    private final ExpressionTree expressionTree;

    private Expression(final PreprocessorContext context, final ExpressionTree tree) {
        this.context = context;
        this.expressionTree = tree;
    }

    private ExpressionTreeElement evalFunction(final ExpressionTreeElement functionElement, final PreprocessingState state) {
        final AbstractFunction function = (AbstractFunction) functionElement.getItem();

        final int arity = function.getArity();
        final Value[] arguments = new Value[arity];
        final Class[] methodArguments = new Class[arity + 1];
        methodArguments[0] = PreprocessorContext.class;

        final StringBuilder signature = new StringBuilder("execute");

        for (int i = 1; i <= arity; i++) {
            methodArguments[i] = Value.class;
        }

        for (int i = 0; i < arity; i++) {
            final ExpressionTreeElement item = calculateTreeElement(functionElement.getChildForIndex(i), state);

            if (item == null) {
                throw new IllegalStateException("There is not needed argument for the \'" + function.getName() + "\' function");
            }

            final ExpressionItem itemValue = item.getItem();

            if (itemValue instanceof Value) {
                arguments[i] = (Value) itemValue;
            } else {
                throw new IllegalStateException("Wrong argument type detected for the \'" + function.getName() + "\' function");
            }
        }

        final ValueType[][] allowedSignatures = function.getAllowedArgumentTypes();
        ValueType[] allowed = null;
        for (final ValueType[] current : allowedSignatures) {
            boolean allCompatible = true;

            int thatIndex = 0;
            for (final ValueType type : current) {
                if (!type.isCompatible(arguments[thatIndex].getType())) {
                    allCompatible = false;
                    break;
                }
                thatIndex++;
            }

            if (allCompatible) {
                allowed = current;
                for (final ValueType type : allowed) {
                    signature.append(type.getSignature());
                }
                break;
            }
        }

        if (allowed == null) {
            throw new IllegalStateException("Unsupported argument set detected for \'" + function.getName() + '\'');
        }

        if (function instanceof FunctionDefinedByUser) {
            final FunctionDefinedByUser userFunction = (FunctionDefinedByUser) function;
            try {
                return new ExpressionTreeElement(userFunction.execute(context, arguments));
            } catch (Exception unexpected) {
                throw new RuntimeException("Unexpected exception during a user function processing", unexpected);
            }
        } else {
            try {
                final Method method = function.getClass().getMethod(signature.toString(), methodArguments);

                final Object[] callArgs = new Object[arity + 1];
                callArgs[0] = context;
                System.arraycopy(arguments, 0, callArgs, 1, arity);

                final Value result = (Value) method.invoke(function, (Object[]) callArgs);

                if (!result.getType().isCompatible(function.getResultType())) {
                    throw new IllegalStateException("Unsupported function result detected [" + result.getType().getSignature() + ']');
                }

                return new ExpressionTreeElement(result);
            } catch (NoSuchMethodException unexpected) {
                throw new RuntimeException("Can't find a function method to process data [" + signature.toString() + ']', unexpected);
            } catch (Exception unexpected) {
                throw new RuntimeException("Can't execute a function method to process data [" + signature.toString() + ']', unexpected);
            }
        }
    }

    private ExpressionTreeElement evalOperator(final ExpressionTreeElement operatorElement, final PreprocessingState state) {
        final AbstractOperator operator = (AbstractOperator) operatorElement.getItem();

        final int arity = operator.getArity();

        final Value[] arguments = new Value[arity];
        final Class[] methodArguments = arity == 1 ? OPERATOR_SIGNATURE_1 : OPERATOR_SIGNATURE_2;

        final StringBuilder signatureNormal = new StringBuilder(AbstractOperator.EXECUTION_PREFIX);
        final StringBuilder signatureAnyLeft = new StringBuilder(AbstractOperator.EXECUTION_PREFIX);
        final StringBuilder signatureAnyRight = new StringBuilder(AbstractOperator.EXECUTION_PREFIX);

        for (int i = 0; i < arity; i++) {
            final ExpressionTreeElement arg = operatorElement.getChildForIndex(i);
            if (arg == null) {
                throw new IllegalStateException("There is not needed argument for the operator [" + operator.getKeyword() + ']');
            }

            final ExpressionTreeElement currentElement = calculateTreeElement(arg, state);

            final ExpressionItem item = currentElement.getItem();

            if (item instanceof Value) {
                arguments[i] = (Value) item;
            } else {
                throw new IllegalStateException("Non-value detected for the \'" + operator.getKeyword() + "\' operator");
            }
        }

        int argIndex = 0;
        for (final Value value : arguments) {
            final String typeSignature = value.getType().getSignature();
            signatureNormal.append(typeSignature);
            if (argIndex == 0) {
                signatureAnyLeft.append(ValueType.ANY.getSignature());
            } else {
                signatureAnyLeft.append(typeSignature);
            }

            if (argIndex == 1) {
                signatureAnyRight.append(ValueType.ANY.getSignature());
            } else {
                signatureAnyRight.append(typeSignature);
            }
            argIndex++;
        }

        Method executeMehod = null;

        try {
            executeMehod = operator.getClass().getMethod(signatureNormal.toString(), methodArguments);
        } catch (NoSuchMethodException ex) {
            try {
                executeMehod = operator.getClass().getMethod(signatureAnyLeft.toString(), methodArguments);
            } catch (NoSuchMethodException ex2) {
                try {
                    executeMehod = operator.getClass().getMethod(signatureAnyRight.toString(), methodArguments);
                } catch (NoSuchMethodException ex3) {
                }
            }
        }

        if (executeMehod == null) {
            throw new IllegalArgumentException("Unsupported arguments detected for operator \'" + operator.getKeyword() + "\' " + Arrays.toString(arguments));
        }

        try {
            return new ExpressionTreeElement((Value) executeMehod.invoke(operator, arguments));
        } catch (ArithmeticException arithEx) {
            throw arithEx;
        } catch (InvocationTargetException ex) {
            final Throwable thr = ex.getTargetException();
            if (thr instanceof ArithmeticException) {
                throw (ArithmeticException) thr;
            }
            throw new RuntimeException("Invocation exception during \'" + operator.getKeyword() + "\' processing", thr);
        } catch (Exception unexpected) {
            throw new RuntimeException("Exception during \'" + operator.getKeyword() + "\' processing", unexpected);
        }
    }

    public static Value evalExpression(final String expression, final PreprocessorContext context, final PreprocessingState state) {
        try {
            final ExpressionTree tree = ExpressionParser.getInstance().parse(expression, context);
            return evalTree(tree, context, state);
        } catch (IOException unexpected) {
            throw new IllegalArgumentException("Wrong expression format detected [" + expression + ']');
        }
    }

    public static Value evalTree(ExpressionTree tree, final PreprocessorContext context, final PreprocessingState state) {
        final Expression exp = new Expression(context, tree);
        return exp.eval(state);
    }

    private ExpressionTreeElement calculateTreeElement(final ExpressionTreeElement element, final PreprocessingState state) {
        ExpressionTreeElement treeElement = element;

        switch (element.getItem().getExpressionItemType()) {
            case VARIABLE: {
                if (context == null) {
                    throw new NullPointerException("Variable can't be used without context [" + element.getItem().toString() + ']');
                }

                final Variable var = (Variable) element.getItem();
                final String name = var.getName();
                final Value value = context.findVariableForName(name, state);
                if (value == null) {
                    throw new RuntimeException("Unknown variable [" + name + ']');
                } else {
                    treeElement = new ExpressionTreeElement(value);
                }
            }
            break;
            case OPERATOR: {
                treeElement = evalOperator(element, state);
            }
            break;
            case FUNCTION: {
                treeElement = evalFunction(element, state);
            }
            break;
        }
        return treeElement;
    }

    private Value eval(final PreprocessingState state) {
        final ExpressionTreeElement result = calculateTreeElement(expressionTree.getRoot(), state);
        final ExpressionItem resultItem = result.getItem();
        if (resultItem instanceof Value) {
            return (Value) resultItem;
        } else {
            throw new IllegalStateException("The expression returns non-value result [" + resultItem + ']');
        }
    }

    public Value eval() {
        return this.eval(null);
    }
}
