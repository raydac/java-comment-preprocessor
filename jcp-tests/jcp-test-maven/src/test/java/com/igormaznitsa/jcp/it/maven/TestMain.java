package com.igormaznitsa.jcp.it.maven;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestMain {

  @Test
  public void testMain(){
      //$new MainTwo();
      //#//
      new Main();
      String str = /*$"\""+some.test.global.test+"\";"$*/ /*-*/"";
      assertEquals("Some Test", str);
      System.out.println(str);
  }

}
