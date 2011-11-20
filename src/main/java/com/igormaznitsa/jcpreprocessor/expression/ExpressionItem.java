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

/**
 * The interface describes an object which can be used during expression calculations
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public interface ExpressionItem {
    /**
     * Get the type of the item
     * @return the item type, must not be null
     */
    ExpressionItemType getExpressionItemType();
    /**
     * Get the priority of the item
     * @return the item priority, must not be null
     */
    ExpressionItemPriority getExpressionItemPriority();
}
