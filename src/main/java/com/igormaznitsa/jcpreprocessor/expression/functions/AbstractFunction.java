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
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemType;
import com.igormaznitsa.jcpreprocessor.expression.ValueType;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractFunction implements ExpressionStackItem {
    public static final AbstractFunction [] ALL_FUNCTIONS = new AbstractFunction[]{
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
    
    protected static final AtomicLong UID_COUNTER = new AtomicLong(1);
    
    public abstract String getName();
    public abstract String getReference();
    public abstract int getArity();
    public abstract ValueType[][] getAllowedArgumentTypes();
    public abstract ValueType getResultType();
    
    public int getPriority() {
        return 5;
    }

    public ExpressionStackItemType getStackItemType() {
        return ExpressionStackItemType.FUNCTION;
    }
    
}
