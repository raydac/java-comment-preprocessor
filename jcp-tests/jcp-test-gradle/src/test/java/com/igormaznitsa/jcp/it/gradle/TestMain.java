package com.igormaznitsa.jcp.it.gradle;

import org.junit.Test;
import org.junit.Assert;

public class TestMain {

  @Test
  public void testMain(){
      Assert.assertEquals("Some Test Global ValueHUZZAA!", new MainTwo().getValue());
  }

}
