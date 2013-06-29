package com.igormaznitsa.jcp.directives;

public class OnlySpacesTest extends AbstractDirectiveHandlerAcceptanceTest {

  @Override
  public void testExecution() throws Exception {
    assertFilePreprocessing("only_spaces.txt", true, null, null);
  }

  @Override
  public void testKeyword() throws Exception {
  }

  @Override
  public void testExecutionCondition() throws Exception {
  }

  @Override
  public void testReference() throws Exception {
  }

  @Override
  public void testArgumentType() throws Exception {
  }

  @Override
  public void testPhase() throws Exception {
  }
}
