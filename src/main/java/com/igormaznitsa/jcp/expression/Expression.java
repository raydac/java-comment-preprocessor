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

import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.expression.functions.AbstractFunction;
import com.igormaznitsa.jcp.expression.functions.FunctionDefinedByUser;
import com.igormaznitsa.jcp.expression.operators.AbstractOperator;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * The main class to calculate expressions
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class Expression {

  /**
   * Precreated array for speed up operations
   */
  private static final Class<?>[] OPERATOR_SIGNATURE_1 = new Class<?>[]{Value.class};

  /**
   * Precreated array for speed up operations
   */
  private static final Class<?>[] OPERATOR_SIGNATURE_2 = new Class<?>[]{Value.class, Value.class};

  /**
   * The variable contains the preprocessor context for the expression, it can
   * be null
   */
  private final PreprocessorContext context;

  /**
   * The variable contains the expression tree
   */
  private final ExpressionTree expressionTree;

  /**
   * Evaluate the expression
   *
   * @return the result as a Value object, it can't be null
   */
  public Value eval() {
    return this.eval(null);
  }

  /**
   * Evaluate expression
   *
   * @param expression the expression as a String, must not be null
   * @param context a preprocessor context to be used for expression operations,
   * it can be null
   * @return the result as a Value object, it can't be null
   */
  public static Value evalExpression(final String expression, final PreprocessorContext context) {
    try {
      final ExpressionTree tree = ExpressionParser.getInstance().parse(expression, context);
      return evalTree(tree, context);
    }
    catch (IOException unexpected) {
      throw context.makeException("[Expression]Wrong expression format detected [" + expression + ']',unexpected);
    }
  }

  /**
   * Evaluate an expression tree
   *
   * @param tree an expression tree, it must not be null
   * @param context a preprocessor context to be used for expression operations,
   * it can be null
   * @return the result as a Value object, it can't be null
   */
  public static Value evalTree(final ExpressionTree tree, final PreprocessorContext context) {
    final Expression exp = new Expression(context, tree);
    return exp.eval(context == null ? null : context.getPreprocessingState());
  }

  private Expression(final PreprocessorContext context, final ExpressionTree tree) {
    if (tree == null) {
      throw context.makeException("[Expression]The expression tree is null",null);
    }
    this.context = context;
    this.expressionTree = tree;
  }

  private ExpressionTreeElement evalFunction(final ExpressionTreeElement functionElement, final PreprocessingState state) {
    final AbstractFunction function = (AbstractFunction) functionElement.getItem();

    final int arity = function.getArity();
    final Value[] arguments = new Value[arity];
    final Class<?>[] methodArguments = new Class<?>[arity + 1];
    methodArguments[0] = PreprocessorContext.class;

    final FilePositionInfo [] stack;
    final String sources;
    
    if (state == null){
      stack = PreprocessingState.EMPTY_STACK;
      sources = "";
    }else{
      stack = state.makeIncludeStack();
      sources = state.getLastReadString();
    }
    
    final StringBuilder signature = new StringBuilder(AbstractFunction.EXECUTION_PREFIX);

    for (int i = 1; i <= arity; i++) {
      methodArguments[i] = Value.class;
    }

    for (int i = 0; i < arity; i++) {
      final ExpressionTreeElement item = calculateTreeElement(functionElement.getChildForIndex(i), state);

      if (item == null) {
        throw this.context.makeException("[Expression]There is not needed argument for the \'" + function.getName() + "\' function",null);
      }

      final ExpressionItem itemValue = item.getItem();

      if (itemValue instanceof Value) {
        arguments[i] = (Value) itemValue;
      }
      else {
        throw this.context.makeException("[Expression]Wrong argument type detected for the \'" + function.getName() + "\' function",null);
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
      throw this.context.makeException("[Expression]Unsupported argument detected for \'" + function.getName() + '\'', null);
    }

    if (function instanceof FunctionDefinedByUser) {
      final FunctionDefinedByUser userFunction = (FunctionDefinedByUser) function;
      try {
        return new ExpressionTreeElement(userFunction.execute(context, arguments),stack, sources);
      }
      catch (Exception unexpected) {
        throw this.context.makeException("[Expression]Unexpected exception during a user function processing", unexpected);
      }
    }
    else {
      try {
        final Method method = function.getClass().getMethod(signature.toString(), methodArguments);

        final Object[] callArgs = new Object[arity + 1];
        callArgs[0] = context;
        System.arraycopy(arguments, 0, callArgs, 1, arity);

        final Value result = (Value) method.invoke(function, callArgs);

        if (!result.getType().isCompatible(function.getResultType())) {
          throw this.context.makeException("[Expression]Unsupported function result detected [" + result.getType().getSignature() + ']',null);
        }

        return new ExpressionTreeElement(result, stack, sources);
      }
      catch (NoSuchMethodException unexpected) {
        throw this.context.makeException("[Expression]Can't find a function method to process data [" + signature.toString() + ']', unexpected);
      }
      catch (Exception unexpected) {
        throw this.context.makeException("[Expression]Can't execute a function method to process data [" + function.getClass().getName()+'.'+signature.toString() + ']', unexpected);
      }
    }
  }

  private ExpressionTreeElement evalOperator(final ExpressionTreeElement operatorElement, final PreprocessingState state) {
    final AbstractOperator operator = (AbstractOperator) operatorElement.getItem();

    final int arity = operator.getArity();

    final Value[] arguments = new Value[arity];
    final Class<?>[] methodArguments = arity == 1 ? OPERATOR_SIGNATURE_1 : OPERATOR_SIGNATURE_2;

    final StringBuilder signatureNormal = new StringBuilder(AbstractOperator.EXECUTION_PREFIX);
    final StringBuilder signatureAnyLeft = new StringBuilder(AbstractOperator.EXECUTION_PREFIX);
    final StringBuilder signatureAnyRight = new StringBuilder(AbstractOperator.EXECUTION_PREFIX);

    final FilePositionInfo [] stack;
    final String sources;
    if (state == null){
      stack = PreprocessingState.EMPTY_STACK;
      sources = "";
    }else{
      stack = state.makeIncludeStack();
      sources = state.getLastReadString();
    }
    
    for (int i = 0; i < arity; i++) {
      final ExpressionTreeElement arg = operatorElement.getChildForIndex(i);
      if (arg == null) {
        throw this.context.makeException("[Expression]There is not needed argument for the operator [" + operator.getKeyword() + ']',null);
      }

      final ExpressionTreeElement currentElement = calculateTreeElement(arg, state);

      final ExpressionItem item = currentElement.getItem();

      if (item instanceof Value) {
        arguments[i] = (Value) item;
      }
      else {
        throw this.context.makeException("[Expression]Non-value detected for the \'" + operator.getKeyword() + "\' operator",null);
      }
    }

    int argIndex = 0;
    for (final Value value : arguments) {
      final String typeSignature = value.getType().getSignature();
      signatureNormal.append(typeSignature);
      if (argIndex == 0) {
        signatureAnyLeft.append(ValueType.ANY.getSignature());
      }
      else {
        signatureAnyLeft.append(typeSignature);
      }

      if (argIndex == 1) {
        signatureAnyRight.append(ValueType.ANY.getSignature());
      }
      else {
        signatureAnyRight.append(typeSignature);
      }
      argIndex++;
    }

    Method executeMehod = null;

    try {
      executeMehod = operator.getClass().getMethod(signatureNormal.toString(), methodArguments);
    }
    catch (NoSuchMethodException ex) {
      try {
        executeMehod = operator.getClass().getMethod(signatureAnyLeft.toString(), methodArguments);
      }
      catch (NoSuchMethodException ex2) {
        try {
          executeMehod = operator.getClass().getMethod(signatureAnyRight.toString(), methodArguments);
        }
        catch (NoSuchMethodException ex3) {
        }
      }
    }

    if (executeMehod == null) {
      throw this.context.makeException("[Expression]Unsupported arguments detected for operator \'" + operator.getKeyword() + "\' " + Arrays.toString(arguments),null);
    }

    try {
      return new ExpressionTreeElement((Value) executeMehod.invoke(operator, (Object[]) arguments), stack, sources);
    }
    catch (ArithmeticException arithEx) {
      throw arithEx;
    }
    catch (InvocationTargetException ex) {
      final Throwable thr = ex.getTargetException();
      if (thr instanceof ArithmeticException) {
        throw (ArithmeticException) thr;
      }
      throw new RuntimeException("Invocation exception during \'" + operator.getKeyword() + "\' processing", thr);
    }
    catch (Exception unexpected) {
      throw this.context.makeException("[Exception]Exception during \'" + operator.getKeyword() + "\' processing", unexpected);
    }
  }

  private ExpressionTreeElement calculateTreeElement(final ExpressionTreeElement element, final PreprocessingState state) {
    ExpressionTreeElement treeElement = element;

    switch (element.getItem().getExpressionItemType()) {
      case VARIABLE: {
        PreprocessorUtils.assertNotNull("[Expression]Variable can't be used without context [" + element.getItem().toString() + ']',context);

        final Variable var = (Variable) element.getItem();
        final String name = var.getName();
        final Value value = context.findVariableForName(name);
        if (value == null) {
          throw new RuntimeException("Unknown variable [" + name + ']');
        }
        else {
          treeElement = new ExpressionTreeElement(value, state.makeIncludeStack(), state.getLastReadString());
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
    if (expressionTree.isEmpty()) {
      throw this.context.makeException("[Expression]The expression is empty",null);
    }
    final ExpressionTreeElement result = calculateTreeElement(expressionTree.getRoot(), state);
    final ExpressionItem resultItem = result.getItem();

    if (resultItem == null) {
      throw this.context.makeException("[Expression]Expression doesn't have result",null);
    }

    if (resultItem instanceof Value) {
      return (Value) resultItem;
    }
    else {
      throw this.context.makeException("[Expression]The expression returns non-value result [" + resultItem + ']',null);
    }
  }

}
