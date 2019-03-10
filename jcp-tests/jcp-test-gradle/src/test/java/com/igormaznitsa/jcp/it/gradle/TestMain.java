package com.igormaznitsa.jcp.it.gradle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestMain {

  @Test
  public void testMain(){
      assertEquals("Some Test", new MainTwo().getValue());
  }

}
