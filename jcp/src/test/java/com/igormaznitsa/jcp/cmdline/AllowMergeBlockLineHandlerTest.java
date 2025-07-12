package com.igormaznitsa.jcp.cmdline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import com.igormaznitsa.jcp.context.PreprocessorContext;

public class AllowMergeBlockLineHandlerTest extends AbstractCommandLineHandlerTest {

  private static final AllowMergeBlockLineHandler HANDLER = new AllowMergeBlockLineHandler();

  @Override
  public void testThatTheHandlerInTheHandlerList() {
    assertHandlerInTheHandlerList(HANDLER);
  }

  @Override
  public void testExecution() throws Exception {
    final PreprocessorContext mock = prepareMockContext();

    assertFalse(HANDLER.processCommandLineKey("", mock));
    assertFalse(HANDLER.processCommandLineKey("/b:", mock));
    assertFalse(HANDLER.processCommandLineKey("/BB", mock));

    assertTrue(HANDLER.processCommandLineKey("/B", mock));
    verify(mock).setAllowsBlocks(true);
  }

  @Override
  public void testName() {
    assertEquals("/B", HANDLER.getKeyName());
  }

  @Override
  public void testDescription() {
    assertDescription(HANDLER);
  }

}