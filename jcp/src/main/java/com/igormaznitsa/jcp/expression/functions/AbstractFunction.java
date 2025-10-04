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

package com.igormaznitsa.jcp.expression.functions;

import com.igormaznitsa.jcp.expression.ExpressionItem;
import com.igormaznitsa.jcp.expression.ExpressionItemPriority;
import com.igormaznitsa.jcp.expression.ExpressionItemType;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_ATTR;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_GET;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_LIST;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_NAME;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_OPEN;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_ROOT;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_SIZE;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_TEXT;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_XELEMENT;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_XLIST;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * The abstract class is the base for each function handler in the preprocessor
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public abstract class AbstractFunction implements ExpressionItem {

  public static final Set<Integer> ARITY_0 = Set.of(0);
  public static final Set<Integer> ARITY_1 = Set.of(1);
  public static final Set<Integer> ARITY_1_2 = Set.of(1, 2);
  public static final Set<Integer> ARITY_2 = Set.of(2);
  public static final Set<Integer> ARITY_3 = Set.of(3);

  /**
   * The string contains the prefix for all executing methods of functions
   */
  public static final String EXECUTION_PREFIX = "execute";

  /**
   * Internal counter to generate UID for some cases
   */
  protected static final AtomicLong UID_COUNTER = new AtomicLong(1);

  /**
   * Current internal map contains all preprocessor functions, mapped by their names
   */
  private static final AtomicReference<Map<String, AbstractFunction>> allFunctions =
      new AtomicReference<>();

  @SuppressWarnings("StaticInitializerReferencesSubClass")
  private static final List<AbstractFunction> DEFAULT_INTERNAL_FUNCTIONS = List.of(
      new FunctionABS(),
      new FunctionROUND(),
      new FunctionESC(),
      new FunctionSTR2INT(),
      new FunctionSTR2WEB(),
      new FunctionSTR2CSV(),
      new FunctionSTR2JS(),
      new FunctionSTR2JSON(),
      new FunctionSTR2XML(),
      new FunctionSTR2JAVA(),
      new FunctionSTR2GO(),
      new FunctionTRIMLINES(),
      new FunctionSTRLEN(),
      new FunctionISSUBSTR(),
      new FunctionIS(),
      new FunctionEVALFILE(),
      new FunctionBINFILE(),
      new FunctionXML_GET(),
      new FunctionXML_SIZE(),
      new FunctionXML_ATTR(),
      new FunctionXML_ROOT(),
      new FunctionXML_NAME(),
      new FunctionXML_LIST(),
      new FunctionXML_TEXT(),
      new FunctionXML_OPEN(),
      new FunctionXML_XLIST(),
      new FunctionXML_XELEMENT()
  );
  private static final Map<String, AbstractFunction> DEFAULT_INTERNAL_FUNCTIONS_MAP =
      DEFAULT_INTERNAL_FUNCTIONS.stream()
          .collect(Collectors.toMap(AbstractFunction::getName, x -> x));

  public static List<AbstractFunction> findAllFunctions() {
    Map<String, AbstractFunction> currentAllFunctions = allFunctions.get();
    if (currentAllFunctions == null) {
      return DEFAULT_INTERNAL_FUNCTIONS;
    } else {
      return currentAllFunctions.values().stream()
          .sorted(Comparator.comparing(AbstractFunction::getName))
          .collect(Collectors.toList());
    }
  }

  public static Map<String, AbstractFunction> getFunctionNameMap() {
    final Map<String, AbstractFunction> result = allFunctions.get();
    if (result == null) {
      return DEFAULT_INTERNAL_FUNCTIONS_MAP;
    }
    return result;
  }

  /**
   * Allows to find a function handler instance for its class
   *
   * @param <E>           the class of the needed function handler extends the
   *                      AbstractFunction class
   * @param functionClass the class of the needed handler, must not be null
   * @return an instance of the needed handler or null if there is not any such
   * one
   */
  public static <E extends AbstractFunction> E findForClass(final Class<E> functionClass) {
    E result = null;
    for (final AbstractFunction function : findAllFunctions()) {
      if (function.getClass() == functionClass) {
        result = functionClass.cast(function);
        break;
      }
    }
    return result;
  }

  /**
   * Find a function handler for its name
   *
   * @param functionName the function name, must not be null
   * @return an instance of the needed handler or null if there is not any such
   * one
   */
  public static AbstractFunction findForName(final String functionName) {
    return getFunctionNameMap().get(functionName);
  }

  /**
   * Get the function name
   *
   * @return the function name in lower case, must not be null
   */
  public abstract String getName();

  /**
   * Get the function reference to be output for a help request
   *
   * @return the function information as a String, must not be null
   */
  public abstract String getReference();

  /**
   * Get the function arities
   *
   * @return all allowed arities for the function
   * @since 7.3.0
   */
  public abstract Set<Integer> getArity();

  /**
   * Get arrays of supported argument types
   *
   * @return list of argument type combinations allowed by the function
   * handler, must not be null
   */
  public abstract List<List<ValueType>> getAllowedArgumentTypes();

  /**
   * Get the result type
   *
   * @return the result type of the function, must not be null
   */
  public abstract ValueType getResultType();

  /**
   * Get the priority of the function in the expression tree
   *
   * @return the expression item priority for the function, must not be null
   */
  @Override
  public ExpressionItemPriority getExpressionItemPriority() {
    return ExpressionItemPriority.FUNCTION;
  }

  /**
   * Get the expression item type
   *
   * @return the expression item type, in the case it is always
   * ExpressionItemType.FUNCTION
   */
  @Override
  public ExpressionItemType getExpressionItemType() {
    return ExpressionItemType.FUNCTION;
  }

  @Override
  public String toString() {
    return "FUNCTION: " + getName();
  }
}
