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

public enum ExpressionStackItemPriority {
    LOGICAL(0),
    COMPARISON(1),
    ARITHMETIC_ADD_SUB(2),
    ARITHMETIC_MUL_DIV_MOD(3),
    FUNCTION(5),
    VALUE(6);
    
    private final int priority;
    
    public int getPriority() {
        return priority;
    }
    
    private ExpressionStackItemPriority(final int priority) {
        this.priority = priority;
    }
}
