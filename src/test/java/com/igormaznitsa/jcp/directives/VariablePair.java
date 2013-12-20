package com.igormaznitsa.jcp.directives;

import com.igormaznitsa.jcp.expression.Value;

public class VariablePair {
  private final String name;
  private final Value value;
  
  public VariablePair(final String name, final String value){
    this.name = name;
    this.value = Value.recognizeOf(value);
  }
          
  public String getName(){
    return this.name;
  }

  public Value getValue(){
    return this.value;
  }
}
