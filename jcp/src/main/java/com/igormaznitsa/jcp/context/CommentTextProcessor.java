package com.igormaznitsa.jcp.context;

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
   * @param context           the current preprocessor context; must not be null
   * @param recommendedIndent the suggested indentation level for the processed text, if any modifications are applied
   * @param uncommentedText   the text that was uncommented and is subject to processing; must not be null
   * @return the processed text, which may be unchanged or modified
   * @since 7.3.0
   */
  String processUncommentedText(
      PreprocessorContext context,
      int recommendedIndent,
      String uncommentedText);

}
