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

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

public final class FunctionABS extends AbstractFunction {

    private static final ValueType [][] ARGUMENT_TYPES = new ValueType[][]{{ValueType.INT},{ValueType.FLOAT}};
    
    @Override
    public String getName() {
        return "abs";
    }

    public Value executeInt(final PreprocessorContext context, final Value value) {
        return Value.valueOf(Long.valueOf((Math.abs(value.asLong().longValue()))));
    }
    
    public Value executeFloat(final PreprocessorContext context, final Value value) {
        return Value.valueOf(Float.valueOf((Math.abs(value.asFloat().floatValue()))));
    }
    
    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public ValueType[][] getAllowedArgumentTypes() {
        return ARGUMENT_TYPES;
    }

    @Override
    public String getReference() {
        return "it returns the absolute value of an argument";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.ANY;
    }


}
