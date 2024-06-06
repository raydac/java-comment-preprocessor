package com.igormaznitsa.jcp.it;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MainTest {
  @Test
  void testMain() {
    Assertions.assertEquals("Hello jcp!", Main.makeHello());
  }
}