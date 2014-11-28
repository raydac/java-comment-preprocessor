//#-
package com.igormaznitsa.jcp;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;
//#+
//#ifdefined includemeth
public final class TestEval {
  public void main(String ... args){
//#endif
    System.out.println("/*$hello_world$*/");
//#ifdefined includemeth
  }
}
//#endif
