package com.igormaznitsa.jcp.cmdline;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

public class KeepLineHandlerTest extends AbstractCommandLineHandlerTest {
  private static final KeepLineHandler HANDLER = new KeepLineHandler();

  @Override
  public void testThatTheHandlerInTheHandlerList() {
    assertHandlerInTheHandlerList(HANDLER);
  }

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext mock = mock(PreprocessorContext.class);

    assertFalse(HANDLER.processCommandLineKey("/k:", mock));
    assertFalse(HANDLER.processCommandLineKey("/KK", mock));
    assertFalse(HANDLER.processCommandLineKey("/K ", mock));
    verify(mock, never()).setKeepLines(anyBoolean());

    assertTrue(HANDLER.processCommandLineKey("/K", mock));
    verify(mock).setKeepLines(true);
    reset(mock);

    assertTrue(HANDLER.processCommandLineKey("/k", mock));
    verify(mock).setKeepLines(true);
    reset(mock);
  }

  @Override
  public void testName() {
    assertEquals("/K", HANDLER.getKeyName());
  }

  @Override
  public void testDescription() {
    assertDescription(HANDLER);
  }
}
