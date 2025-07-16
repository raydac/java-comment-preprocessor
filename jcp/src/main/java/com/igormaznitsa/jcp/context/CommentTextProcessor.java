package com.igormaznitsa.jcp.context;

import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;

/**
 * Custom processor to detect and process uncommenting text line or text block
 * defined through //$ and //$$ directives. It can return the same value or
 * changed one. The processor is called after ext prepared for injection into preprocessed source.
 * If activated merge block mode for preprocessor so sequentially
 * situated lines started with """ will be merged into single text block (and """ will be removed)
 *
 * @since 7.2.0
 */
public interface CommentTextProcessor extends PreprocessorContextListener {

  /**
   * Process uncommented text detected in //$ or //$$ sections. If processing not needed then the provided text must be returned.
   *
   * @param recommendedIndent indent to be recommended for the processed text if it will be processed
   * @param uncommentedText   the text which was uncommented and needs processing, must not be null
   * @param fileContainer     the source file info container calling the processor, must not be null
   * @param positionInfo      position of the uncommented line or the first line of the uncommented text block, must not be null
   * @param context           the current preprocessor context, must not be null
   * @param state             the current preprocess state, must not be null
   * @return must return value as the same text or as the changed one.
   * @since 7.2.1
   */
  String processUncommentedText(
      int recommendedIndent,
      String uncommentedText,
      FileInfoContainer fileContainer, FilePositionInfo positionInfo,
      PreprocessorContext context,
      PreprocessingState state);
}
