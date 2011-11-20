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
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItem;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemPriority;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemType;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractFunction implements ExpressionStackItem {
    private static final AbstractFunction [] ALL_FUNCTIONS = new AbstractFunction[]{
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
    
    protected static final AtomicLong UID_COUNTER = new AtomicLong(1);

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
    
    public abstract String getName();
    public abstract String getReference();
    public abstract int getArity();
    public abstract ValueType[][] getAllowedArgumentTypes();
    public abstract ValueType getResultType();
    
    public ExpressionStackItemPriority getPriority() {
        return ExpressionStackItemPriority.FUNCTION;
    }

    public ExpressionStackItemType getStackItemType() {
        return ExpressionStackItemType.FUNCTION;
    }
    
    @Override
    public String toString() {
        return "FUNCTION: "+getName();
    }
}
