package com.igormaznitsa.jcp.directives;

public class LinesNotMatchException extends RuntimeException {
  private static final long serialVersionUID=0x129894723894A123L;
  
  private final int etalonLineNumber;
  private final int resultLineNumber;
  private final int problemStringIndex;
  private final String etalonString;
  private final String resultString;
  
  public LinesNotMatchException(final int etalonLineNumber, final int resultLineNumber, final int problemStringIndex, final String etalonString, final String resultString){
    super("Lines not match in the etalon and the result");
    this.etalonLineNumber = etalonLineNumber;
    this.resultLineNumber = resultLineNumber;
    this.etalonString = etalonString;
    this.resultString = resultString;
    this.problemStringIndex = problemStringIndex;
  }
  
  public int getProblemStringIndex(){
    return this.problemStringIndex;
  }
  
  public int getEtalonLineNumber(){
    return this.etalonLineNumber;
  }
  
  public int getResultLineNumber(){
    return this.resultLineNumber;
  }
  
  public String getEtalonString(){
    return this.etalonString;
  }
  
  public String getResultString(){
    return this.resultString;
  }
  
  @Override
  public String toString(){
    return LinesNotMatchException.class.getName()+"(etalonLineNum="+this.etalonLineNumber
            +",resultLineNum="+this.resultLineNumber
            +",problemLine"+(this.problemStringIndex+1)
            +",etalonString="+this.etalonString
            +",resultString="+this.resultString
            +')';
  }
}
