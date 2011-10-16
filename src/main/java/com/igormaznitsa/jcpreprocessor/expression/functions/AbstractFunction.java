package com.igormaznitsa.jcpreprocessor.expression.functions;

import com.igormaznitsa.jcpreprocessor.expression.AbstractExpressionExecutor;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItem;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemType;

public abstract class AbstractFunction implements AbstractExpressionExecutor, ExpressionStackItem {
    public static final AbstractFunction [] ALL_FUNCTIONS = new AbstractFunction[]{
      new FunctionABS(),  
      new FunctionROUND(),  
      new FunctionSTR2INT(),  
      new FunctionSTR2WEB(),  
      new FunctionSTRLEN(),  
      new FunctionXML_ELEMENTAT(),  
      new FunctionXML_ELEMENTSNUMBER(),  
      new FunctionXML_GETATTRIBUTE(),  
      new FunctionXML_GETDOCUMENTELEMENT(),  
      new FunctionXML_GETELEMENTNAME(),  
      new FunctionXML_GETELEMENTSFORNAME(),  
      new FunctionXML_GETELEMENTTEXT(),  
      new FunctionXML_OPEN()  
    };
    
    public abstract String getName();

    public int getPriority() {
        return 5;
    }

    public ExpressionStackItemType getStackItemType() {
        return ExpressionStackItemType.FUNCTION;
    }
    
    public abstract int getArity();
}
