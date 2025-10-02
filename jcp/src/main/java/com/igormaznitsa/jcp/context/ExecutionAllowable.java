package com.igormaznitsa.jcp.context;

/**
 * Interface describes an abstract object which can decide to be executed or not during execution in specified point.
 *
 * @since 7.3.0
 */
public interface ExecutionAllowable {

  /**
   * Indicates whether execution is allowed to run in the current context.
   * This method is invoked before each call and receives complete
   * information about the current context and source file, enabling it to make a
   * dynamic decision.
   * <b>If execution in bounds of test or mock state then some arguments can be null.</b>
   *
   * @param context the current preprocessor context; must not be null
   * @return {@code true} if it is allowed to run; {@code false} otherwise
   */
  boolean isAllowed(
      PreprocessorContext context
  );
}
