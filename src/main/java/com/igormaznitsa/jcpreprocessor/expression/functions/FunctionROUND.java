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
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;

public final class FunctionROUND extends AbstractFunction {

    private static final ValueType [][] SIGNATURES = new ValueType[][]{{ValueType.FLOAT},{ValueType.INT}};
    
    @Override
    public String getName() {
        return "round";
    }

    public Value executeInt(final PreprocessorContext context, final Value value) {
        return value;
    }
    
    public Value executeFloat(final PreprocessorContext context, final Value value) {
        return Value.valueOf(Long.valueOf(Math.round(value.asFloat())));
    }
    
    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public ValueType[][] getAllowedArgumentTypes() {
        return SIGNATURES;
    }

    @Override
    public String getReference() {
        return "it returns closest integer value to the argument";
    }

    @Override
    public ValueType getResultType() {
        return ValueType.INT;
    }
    
    
}
