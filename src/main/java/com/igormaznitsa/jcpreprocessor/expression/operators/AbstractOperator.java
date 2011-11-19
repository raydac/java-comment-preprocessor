package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItem;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemType;

public abstract class AbstractOperator implements ExpressionStackItem {

    public static final String EXECUTION_PREFIX = "execute";
    
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
      new OperatorXOR()
    };
    
    public static <E extends AbstractOperator> E findForClass(final Class<E> operatorClass) {
        for(final AbstractOperator operator : ALL_OPERATORS){
            if (operator.getClass() == operatorClass) {
                return (E)operator;
            }
        }
        return null;
    } 
    
    public ExpressionStackItemType getStackItemType() {
        return ExpressionStackItemType.OPERATOR;
    }
    
    public abstract int getArity();
    public abstract String getKeyword();
    public abstract String getReference();
    
    @Override
    public String toString(){
        return "OPERATOR: "+getKeyword();
    }
}
