package com.igormaznitsa.jcp.context;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Type of comment remover.
 * @since 7.1.0
 */
public enum CommentRemoverType {
  /**
   * To not remove comments.
   */
  KEEP_ALL,
  /**
   * Remove all single line and multiline comments defined in C style.
   */
  REMOVE_C_STYLE,
  /**
   * Remove only comments contain JCP directives.
   */
  REMOVE_JCP_ONLY;

  /**
   * Make comma separated list of enum item names.
   * @return comma separated list of all enum items, without spaces.
   */
  public static String makeListOfAllRemoverIds() {
    return Arrays.stream(CommentRemoverType.values())
        .map(Enum::toString).collect(Collectors.joining(","));
  }

}
