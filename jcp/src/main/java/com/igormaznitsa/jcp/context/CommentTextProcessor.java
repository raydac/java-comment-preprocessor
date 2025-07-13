package com.igormaznitsa.jcp.context;

import com.igormaznitsa.jcp.containers.FileInfoContainer;
import java.io.IOException;

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
   * Process text value.
   *
   * @param text              the source text
   * @param fileInfoContainer the source file info container, must not be null
   * @param context           the source preprocessor context, must not be null
   * @param state             the current preprocess state, must not be null
   * @return must return value as the same text or as the changed one.
   * @throws IOException if any IO error during operation
   */
  String onUncommentText(
      String text,
      FileInfoContainer fileInfoContainer,
      PreprocessorContext context,
      PreprocessingState state) throws IOException;
}
