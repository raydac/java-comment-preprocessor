// #outname "MainTwo.java"
package com.igormaznitsa.jcp.it.maven;

//$public class MainTwo {
//#//
//#action 1,2,3
public class Main {

  //$$public MainTwo(){
  //#//
  public Main(){
    if (!this.getClass().getName().endsWith("MainTwo")) {
      throw new Error("Must be MainTwo but detected"+this.getClass().getName());
    }

    //#if empty.null.variable
    throw new Error("Must not be presented because variable must be recognized as false");
    //#endif

    final String test = /*$"\""+some.test.global+"\";"$*/ /*-*/ "";

    if ("Some Test Global Value".equals(test)){
      System.out.println("All ok");
    } else {
      throw new Error("Unexpected value: "+test);
    }
  }

  //#action $hello1("getProperty")
  public String getProperty() {
    return "/*$mvn.project.property.my.custom.property$*/";
  }
}
