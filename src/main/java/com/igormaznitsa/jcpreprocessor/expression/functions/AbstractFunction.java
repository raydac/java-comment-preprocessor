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
package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.expression.functions.xml.FunctionXML_GETELEMENTTEXT;
import com.igormaznitsa.jcpreprocessor.expression.functions.xml.FunctionXML_GETATTRIBUTE;
import com.igormaznitsa.jcpreprocessor.expression.functions.xml.FunctionXML_GETELEMENTSFORNAME;
import com.igormaznitsa.jcpreprocessor.expression.functions.xml.FunctionXML_ELEMENTAT;
import com.igormaznitsa.jcpreprocessor.expression.functions.xml.FunctionXML_ELEMENTSNUMBER;
import com.igormaznitsa.jcpreprocessor.expression.functions.xml.FunctionXML_OPEN;
import com.igormaznitsa.jcpreprocessor.expression.functions.xml.FunctionXML_GETROOT;
import com.igormaznitsa.jcpreprocessor.expression.functions.xml.FunctionXML_GETELEMENTNAME;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionItem;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionItemType;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
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
    public static final AbstractFunction [] ALL_FUNCTIONS = new AbstractFunction[]{
      new FunctionABS(),  
      new FunctionROUND(),  
      new FunctionSTR2INT(),  
      new FunctionSTR2WEB(),  
      new FunctionSTRLEN(),  
      new FunctionXML_ELEMENTAT(),  
      new FunctionXML_ELEMENTSNUMBER(),  
      new FunctionXML_GETATTRIBUTE(),  
      new FunctionXML_GETROOT(),  
      new FunctionXML_GETELEMENTNAME(),  
      new FunctionXML_GETELEMENTSFORNAME(),  
      new FunctionXML_GETELEMENTTEXT(),  
      new FunctionXML_OPEN()  
    };
    
    /**
     * Allows to find a function handler instance for its class
     * @param <E> the class of the needed function handler extends the AbstractFunction class
     * @param functionClass the class of the needed handler, must not be null
     * @return an instance of the needed handler or null if there is not any such one
     */
    public static <E extends AbstractFunction> E findForClass(final Class<E> functionClass) {
        E result = null;
        for(final AbstractFunction function : ALL_FUNCTIONS){
            if (function.getClass() == functionClass) {
                result = (E)function;
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
     * @param str the function name, must not be null
     * @return an instance of the needed handler or null if there is not any such one
     */
    public static AbstractFunction findForName(final String str) {
        AbstractFunction result = null;
        for(final AbstractFunction func : ALL_FUNCTIONS){
            if (func.getName().equals(str)){
                result = func;
                break;
            }
        }
        return result;
    }
    
    /**
     * Get the function name
     * @return the function name in lower case, must not be null
     */
    public abstract String getName();
    
    /**
     * Get the function reference to be output for a help request
     * @return the function information as a String, must not be null
     */
    public abstract String getReference();
    
    /**
     * Get the function arity
     * @return the function arity (zero or greater)
     */
    public abstract int getArity();
    
    /**
     * Get arrays of supported argument types
     * @return the array of argument type combinations allowed by the function handler, must not be null
     */
    public abstract ValueType[][] getAllowedArgumentTypes();
    
    /**
     * Get the result type
     * @return the result type of the function, must not be null
     */
    public abstract ValueType getResultType();

    /**
     * Get the priority of the function in the expression tree
     * @return the expression item priority for the function, must not be null
     */
    public ExpressionItemPriority getExpressionItemPriority() {
        return ExpressionItemPriority.FUNCTION;
    }

    /**
     * Get the expression item type
     * @return the expression item type, in the case it is always ExpressionItemType.FUNCTION
     */
    public ExpressionItemType getExpressionItemType() {
        return ExpressionItemType.FUNCTION;
    }
    
    @Override
    public String toString() {
        return "FUNCTION: "+getName();
    }
}
