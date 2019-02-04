// this file will be removed by preprocessor from the result because marked as excluded one
//#excludeif true
//#-

package com.igormaznitsa.jcp.it.test;

public class Javassistable {
  public boolean printLines(int ___arg1, String ___arg2, String ___arg3) {
    final int lineCounter = ___arg1;
    final String pattern = ___arg2;
    final String text = ___arg3;
//#+
    //$int lineCounter = $1;
    //$String pattern = $2;
    //$String text = $3;

    if ((lineCounter & 1) != 0) {
      throw new IllegalArgumentException("Line counter must be even one: " + lineCounter);
    }

    boolean flag = false;
    for (int i = 0; i < lineCounter; i++) {
      System.out.println(String.format(pattern, new String[] {text}));
      flag = !flag;
    }

    return flag;
//#-
  }
}
//#+
