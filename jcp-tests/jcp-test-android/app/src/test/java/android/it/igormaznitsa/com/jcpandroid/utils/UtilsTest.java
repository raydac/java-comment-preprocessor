package android.it.igormaznitsa.com.jcpandroid.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {
  @Test
  public void testMakeSecretPassword(){
    assertEquals("testPassword", Utils.makeSecretPassword());
  }
}