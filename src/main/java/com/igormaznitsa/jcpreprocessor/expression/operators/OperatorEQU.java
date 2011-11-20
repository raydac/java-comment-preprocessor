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
 * The class implements the EQU operator handler
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class OperatorEQU extends AbstractOperator {

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public String getReference() {
       return "indicates whether the value of the left operand is equal to the value of the right operand";
    }
    
    @Override
    public String getKeyword() {
        return "==";
    }

    public Value executeIntInt(final Value arg1, final Value arg2) {
        return Value.valueOf(Boolean.valueOf(arg1.asLong().longValue() == arg2.asLong().longValue()));
    }
    
    public Value executeFloatInt(final Value arg1, final Value arg2) {
        return Value.valueOf(Boolean.valueOf(Float.compare(arg1.asFloat().floatValue(), arg2.asLong().floatValue()) == 0));
    }
    
    public Value executeIntFloat(final Value arg1, final Value arg2) {
        return Value.valueOf(Boolean.valueOf(Float.compare(arg1.asLong().floatValue(), arg2.asFloat().floatValue()) == 0));
    }
    
    public Value executeFloatFloat(final Value arg1, final Value arg2) {
        return Value.valueOf(Boolean.valueOf(Float.compare(arg1.asFloat().floatValue(), arg2.asFloat().floatValue()) == 0));
    }
    
    public Value executeStrStr(final Value arg1, final Value arg2) {
        return Value.valueOf(Boolean.valueOf(arg1.asString().equals(arg2.asString())));
    }
    
    public Value executeBoolBool(final Value arg1, final Value arg2) {
         return Value.valueOf(Boolean.valueOf(arg1.asBoolean().booleanValue() == arg2.asBoolean().booleanValue()));
    }

    public ExpressionItemPriority getExpressionItemPriority() {
        return ExpressionItemPriority.COMPARISON;
    }
}
