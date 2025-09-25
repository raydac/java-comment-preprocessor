package com.igormaznitsa.jcp.context;

import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;

/**
 * A custom processor for detecting and handling uncommenting directives in source text.
 * This processor recognizes lines or blocks marked with `//$` and `//$$` and can either
 * return them unchanged or modify them as needed. It is invoked after the external text
 * is prepared for injection into the preprocessed source.
 * If block merging is enabled, consecutive lines beginning with `"""` will be merged
 * into a single text block, and the `"""` markers will be removed.
 *
 * @since 7.2.0
 */
public interface CommentTextProcessor extends PreprocessorContextAware, ExecutionAllowable {

  /**
   * Processes uncommented text detected in `//$` or `//$$` sections.
   * If no transformation is needed, the original text must be returned unchanged.
   *
   * @param recommendedIndent the suggested indentation level for the processed text, if any modifications are applied
   * @param uncommentedText   the text that was uncommented and is subject to processing; must not be null
   * @param fileContainer     the container holding metadata about the source file invoking the processor; must not be null
   * @param positionInfo      the position of the uncommented line or the first line of the uncommented block; must not be null
   * @param context           the current preprocessor context; must not be null
   * @param state             the current preprocessor state; must not be null
   * @return the processed text, which may be unchanged or modified
   * @since 7.2.1
   */
  String processUncommentedText(
      int recommendedIndent,
      String uncommentedText,
      FileInfoContainer fileContainer,
      FilePositionInfo positionInfo,
      PreprocessorContext context,
      PreprocessingState state);

}
