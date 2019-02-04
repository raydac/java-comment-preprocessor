package com.igormaznitsa.jcp.it.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JavassistableTest {

  @Test
  public void testProcessJavassistSrc() throws Exception {
    final JavassistMain main = new JavassistMain();
    main.printLines(4, "Feel power of %s!", "preprocessing");
    Assertions.assertThrows(IllegalArgumentException.class, () ->
        main.printLines(3, "Hello %s!", "Preprocessor"));
  }
}
