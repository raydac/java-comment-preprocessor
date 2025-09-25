package com.igormaznitsa.jcp.context;

/**
 * Listener for preprocessor context work states.
 *
 * @since 7.3.0
 */
public interface PreprocessorContextAware {
  /**
   * Called when context started.
   *
   * @param context the source context, must not be null
   */
  default void onContextStarted(PreprocessorContext context) {

  }

  /**
   * Called when context work ended.
   *
   * @param context the source context, must not be null
   * @param error   the error if it was thrown during context execution.
   */
  default void onContextStopped(PreprocessorContext context, Throwable error) {

  }
}
