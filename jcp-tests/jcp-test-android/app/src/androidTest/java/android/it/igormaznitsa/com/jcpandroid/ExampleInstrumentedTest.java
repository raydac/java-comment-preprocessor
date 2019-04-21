package android.it.igormaznitsa.com.jcpandroid;

import android.content.Context;
import android.it.igormaznitsa.com.jcpandroid.utils.Utils;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
  @Test
  public void useAppContext() {
    Context appContext = InstrumentationRegistry.getTargetContext();
    assertEquals("android.it.igormaznitsa.com.jcpandroid", appContext.getPackageName());
  }
}
