package com.igormaznitsa.jcp.cmdline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import org.junit.Test;

public class ActionPreprocessorExtensionHandlerTest extends AbstractCommandLineHandlerTest {
  private static final ActionPreprocessorExtensionHandler HANDLER =
      new ActionPreprocessorExtensionHandler();

  @Override
  public void testThatTheHandlerInTheHandlerList() {
    assertHandlerInTheHandlerList(HANDLER);
  }

  @Test
  public void testErrorWithoutFlag() {
  }

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext mock = prepareMockContext();

    assertTrue(HANDLER.processCommandLineKey("/EA:", mock));
    assertFalse(HANDLER.processCommandLineKey("/AE", mock));
    assertFalse(HANDLER.processCommandLineKey("/E ", mock));
    verify(mock, never()).setAllowWhitespaces(anyBoolean());

    assertFalse(HANDLER.processCommandLineKey("/EA", mock));
    reset(mock);
  }

  @Override
  public void testName() {
    assertEquals("/EA:", HANDLER.getKeyName());
  }

  @Override
  public void testDescription() {
    assertDescription(HANDLER);
  }

}