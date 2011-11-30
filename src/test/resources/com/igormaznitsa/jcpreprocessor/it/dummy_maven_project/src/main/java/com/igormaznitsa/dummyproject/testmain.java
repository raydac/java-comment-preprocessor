//#outname "testmain2.java"
package com.igormaznitsa.dummyproject;

//#-
public class testmain {
//#+
//$$public class testmain2 {
public static final void main(String ... args){
        System.out.println("Maven project name is /*$mvn.project.name$*/");
        for(final String arg : args){
            System.out.println("Argument : "+arg);
        }
    }
}