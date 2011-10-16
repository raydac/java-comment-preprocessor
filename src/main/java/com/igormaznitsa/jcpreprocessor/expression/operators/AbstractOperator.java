package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.AbstractExpressionExecutor;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItem;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemType;

public abstract class AbstractOperator implements AbstractExpressionExecutor, ExpressionStackItem {

    public static final AbstractOperator [] ALL_OPERATORS = new AbstractOperator[]{
      new OperatorADD(),  
      new OperatorAND(),  
      new OperatorDIV(),  
      new OperatorEQU(),  
      new OperatorGREAT(),  
      new OperatorGREATEQU(),  
      new OperatorLESS(),  
      new OperatorLESSEQU(),  
      new OperatorMOD(),  
      new OperatorMUL(),  
      new OperatorNOT(),  
      new OperatorNOTEQU(),  
      new OperatorOR(),  
      new OperatorSUB(),  
      new OperatorXOR(),
      new OperatorLEFTBRACKET(),
      new OperatorRIGHTBRACKET()
    };
    
    public ExpressionStackItemType getStackItemType() {
        return ExpressionStackItemType.OPERATOR;
    }
    
    public abstract boolean isUnary();
    public abstract String getKeyword();
}
