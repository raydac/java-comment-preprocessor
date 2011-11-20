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
package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionItem;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionItemType;

/**
 * The class is the base for all operator handlers
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public abstract class AbstractOperator implements ExpressionItem {

    /**
     * The constant is the prefix for executing methods of operators
     */
    public static final String EXECUTION_PREFIX = "execute";
    
    /**
     *  The array contains all operators allowed by the preprocessor
     */
    public static final AbstractOperator [] ALL_OPERATORS = new AbstractOperator[]{
      new OperatorEQU(),  
      new OperatorGREAT(),  
      new OperatorGREATEQU(),  
      new OperatorLESS(),  
      new OperatorLESSEQU(),  
      new OperatorNOTEQU(),  
      new OperatorADD(),  
      new OperatorSUB(),  
      new OperatorMUL(),  
      new OperatorDIV(),  
      new OperatorMOD(),  
      new OperatorNOT(),  
      new OperatorAND(),  
      new OperatorOR(),  
      new OperatorXOR(),
    };
    
    /**
     * Find an operator handler for its class
     * @param <E> the handler class extends AbstractOperator
     * @param operatorClass the class to be used for search, must not be null
     * @return an instance of the handler or null if there is not any such one
     */
    public static <E extends AbstractOperator> E findForClass(final Class<E> operatorClass) {
        for(final AbstractOperator operator : ALL_OPERATORS){
            if (operator.getClass() == operatorClass) {
                return (E)operator;
            }
        }
        return null;
    } 
    
    /**
     * Get the expression item type
     * @return for operators it is always ExpressionItemType.OPERATOR
     */
    public ExpressionItemType getExpressionItemType() {
        return ExpressionItemType.OPERATOR;
    }
    
    /**
     * Get the operator arity
     * @return the operator arity (1 or 2)
     */
    public abstract int getArity();
    
    /**
     * Get the operator keyword
     * @return the operator keyword, must not be null
     */
    public abstract String getKeyword();
    
    /**
     * Get the operator reference to be shown for a help information request
     * @return the operator reference as a String, must not be null
     */
    public abstract String getReference();
    
    @Override
    public String toString(){
        return "OPERATOR: "+getKeyword();
    }
}
