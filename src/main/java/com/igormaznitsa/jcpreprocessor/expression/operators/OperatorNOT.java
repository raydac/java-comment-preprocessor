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

import com.igormaznitsa.jcpreprocessor.expression.ExpressionItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.Value;

/**
 * The class implements the NOT operator handler 
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class OperatorNOT extends AbstractOperator {

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public String getReference() {
        return "it makes NOT operation over an operand, bitwise NOT over a numeric operand and logical NOT over a boolean one";
    }

    @Override
    public String getKeyword() {
        return "!";
    }

   public Value executeInt(final Value arg1) {
        return Value.valueOf(Long.valueOf(0xFFFFFFFFFFFFFFFFL^arg1.asLong().longValue()));
    }
    
    public Value executeBool(final Value arg1) {
        return Value.valueOf(Boolean.valueOf(!arg1.asBoolean().booleanValue()));
    }

     public ExpressionItemPriority getExpressionItemPriority() {
        return ExpressionItemPriority.FUNCTION;
    }
}
