package com.igormaznitsa.jcp.it.maven;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestMain {

  @Test
  public void testMain(){
      //$MainTwo obj = new MainTwo();
      //#-
      Main obj = new Main();
      //#+

      assertEquals("some_custom_property", obj.getProperty());
      assertEquals("test_custom_property","/*$mvn.project.property.my.custom.property$*/");

      String str = /*$"\""+some.test.global.test+"\";"$*/ /*-*/"";
      assertEquals("Some Test", str);
      System.out.println(str);
  }

}
