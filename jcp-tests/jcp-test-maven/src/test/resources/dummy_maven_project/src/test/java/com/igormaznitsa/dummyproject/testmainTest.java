package com.igormaznitsa.dummyproject;

import org.junit.Test;
import static org.junit.Assert.*;

public class testmainTest {
  @Test
  public void testTest(){
    assertEquals("/*$mvn.project.name$*/",new testmain2().test());
    System.out.println("PREPROCESSED_TESTING_COMPLETED");
  }
}