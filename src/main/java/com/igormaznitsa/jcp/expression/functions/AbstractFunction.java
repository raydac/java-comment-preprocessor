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
package com.igormaznitsa.jcp.expression.functions;

import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_TEXT;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_ATTR;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_LIST;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_GET;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_SIZE;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_OPEN;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_ROOT;
import com.igormaznitsa.jcp.expression.functions.xml.FunctionXML_NAME;
import com.igormaznitsa.jcp.expression.ExpressionItem;
import com.igormaznitsa.jcp.expression.ExpressionItemPriority;
import com.igormaznitsa.jcp.expression.ExpressionItemType;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.expression.functions.xml.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The abstract class is the base for each function handler in the preprocessor
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public abstract class AbstractFunction implements ExpressionItem {

  /**
   * The string contains the prefix for all executing methods of functions
   */
  public static final String EXECUTION_PREFIX = "execute";

  /**
   * Inside array contains all functions supported by the preprocessor
   */
  public static final AbstractFunction[] ALL_FUNCTIONS = new AbstractFunction[]{
    new FunctionABS(),
    new FunctionROUND(),
    new FunctionSTR2INT(),
    new FunctionSTR2WEB(),
    new FunctionSTR2CSV(),
    new FunctionSTR2JS(),
    new FunctionSTR2JSON(),
    new FunctionSTR2XML(),
    new FunctionSTR2JAVA(),
    new FunctionSTRLEN(),
    new FunctionISSUBSTR(),
    new FunctionEVALFILE(),
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
  };

  /**
   * Allows to find a function handler instance for its class
   *
   * @param <E> the class of the needed function handler extends the
   * AbstractFunction class
   * @param functionClass the class of the needed handler, must not be null
   * @return an instance of the needed handler or null if there is not any such
   * one
   */
  public static <E extends AbstractFunction> E findForClass(final Class<E> functionClass) {
    E result = null;
    for (final AbstractFunction function : ALL_FUNCTIONS) {
      if (function.getClass() == functionClass) {
        result = functionClass.cast(function);
        break;
      }
    }
    return result;
  }

  /**
   * Inside counter to generate UID for some cases
   */
  protected static final AtomicLong UID_COUNTER = new AtomicLong(1);

  /**
   * Find a function handler for its name
   *
   * @param str the function name, must not be null
   * @return an instance of the needed handler or null if there is not any such
   * one
   */
  public static AbstractFunction findForName(final String str) {
    AbstractFunction result = null;
    for (final AbstractFunction func : ALL_FUNCTIONS) {
      if (func.getName().equals(str)) {
        result = func;
        break;
      }
    }
    return result;
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
   * Get the function arity
   *
   * @return the function arity (zero or greater)
   */
  public abstract int getArity();

  /**
   * Get arrays of supported argument types
   *
   * @return the array of argument type combinations allowed by the function
   * handler, must not be null
   */
  public abstract ValueType[][] getAllowedArgumentTypes();

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
