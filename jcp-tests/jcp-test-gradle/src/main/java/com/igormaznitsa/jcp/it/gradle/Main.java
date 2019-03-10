// #outname "MainTwo.java"
package com.igormaznitsa.jcp.it.gradle;

//$public class MainTwo {
//#//
public class Main {

  //$$public MainTwo(){
  //#//
  public Main(){
    if (!this.getClass().getName().endsWith("MainTwo")) {
      throw new Error("Must be MainTwo but detected"+this.getClass().getName());
    }

    final String test = /*$"\""+some.test.global+"\";"$*/ /*-*/ "";

    if ("Some Test Global Value".equals(test)){
      System.out.println("All ok, detected value '/*$some.test.global$*/'");
    } else {
      throw new Error("Unexpected value: "+test);
    }
  }

  public String getValue(){
    return /*$"\""+some.test.global+"\";"$*/ /*-*/ "";
  }
  
  public static void main(String [] args) {
    //$$new MainTwo();
    //#//
    new Main();
  }
}
