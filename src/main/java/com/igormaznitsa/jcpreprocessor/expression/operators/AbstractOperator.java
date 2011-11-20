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

public abstract class AbstractOperator implements ExpressionItem {

    public static final String EXECUTION_PREFIX = "execute";
    
    public static final AbstractOperator [] ALL_OPERATORS = new AbstractOperator[]{
      new OperatorADD(),  
      new OperatorAND(),  
      new OperatorDIV(),  
      new OperatorEQU(),  
      new OperatorGREAT(),  
      new OperatorGREATEQU(),  
      new OperatorLESS(),  
      new OperatorLESSEQU(),  
      new OperatorMOD(),  
      new OperatorMUL(),  
      new OperatorNOT(),  
      new OperatorNOTEQU(),  
      new OperatorOR(),  
      new OperatorSUB(),  
      new OperatorXOR()
    };
    
    public static <E extends AbstractOperator> E findForClass(final Class<E> operatorClass) {
        for(final AbstractOperator operator : ALL_OPERATORS){
            if (operator.getClass() == operatorClass) {
                return (E)operator;
            }
        }
        return null;
    } 
    
    public ExpressionItemType getExpressionItemType() {
        return ExpressionItemType.OPERATOR;
    }
    
    public abstract int getArity();
    public abstract String getKeyword();
    public abstract String getReference();
    
    @Override
    public String toString(){
        return "OPERATOR: "+getKeyword();
    }
}
