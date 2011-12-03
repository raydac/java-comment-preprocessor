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
package com.igormaznitsa.jcp.expression;

/**
 * The class describes an expression variable
 * 
 * @author Igor Mznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class Variable implements ExpressionItem {
    /**
     * The variable contains the expression variable name
     */
    private final String variableName;
    
    /**
     * The constructor
     * @param varName the variable name, it must not be null
     */
    public Variable(final String varName) {
        if (varName == null) {
            throw new NullPointerException("Var name is null");
        }
        this.variableName = varName;
    }
    
    /**
     * Get the variable name
     * @return the name saved by the object
     */
    public String getName() {
        return this.variableName;
    }
    
    /**
     * Get the expression item type
     * @return it returns only ExpressionItemType.VARIABLE
     */
    public ExpressionItemType getExpressionItemType() {
        return ExpressionItemType.VARIABLE;
    }

    /**
     * Get the expression item priority
     * @return it returns only ExpressionItemPriority.VALUE
     */
    public ExpressionItemPriority getExpressionItemPriority() {
        return ExpressionItemPriority.VALUE;
    }
    
}
