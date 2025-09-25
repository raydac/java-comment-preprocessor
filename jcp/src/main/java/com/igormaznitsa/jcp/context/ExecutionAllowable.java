package com.igormaznitsa.jcp.context;

import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;

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
   * @param nullableFileContainer the container holding metadata about the source file invoking the processor, can be null if started not full preprocessing cycle
   * @param nullablePositionInfo  the position of the uncommented line or the first line of the uncommented block,  can be null if started not full preprocessing cycle
   * @param context               the current preprocessor context; must not be null
   * @param state                 the current preprocessor state; must not be null
   * @return {@code true} if it is allowed to run; {@code false} otherwise
   */
  boolean isAllowed(
      FileInfoContainer nullableFileContainer,
      FilePositionInfo nullablePositionInfo,
      PreprocessorContext context,
      PreprocessingState state
  );
}
