package com.igormaznitsa.jcpreprocessor.expression.operators;

import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItem;
import com.igormaznitsa.jcpreprocessor.expression.ExpressionStackItemType;

public abstract class AbstractOperator implements ExpressionStackItem {

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
    
    public abstract void execute(PreprocessorContext context, Expression stack, int index); 
    public abstract boolean isUnary();
    public abstract String getKeyword();
}
