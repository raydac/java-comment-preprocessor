//   #outname "testmain2.java"
package com.igormaznitsa.dummyproject;

//#-
public class testmain {
//#+
//  $$public class testmain2 {
public String test(){
//#action "hello","world",$call1(1)
        return "/*$ant.ant.project.name+globalvar+cfg.test$*/";
    }
}