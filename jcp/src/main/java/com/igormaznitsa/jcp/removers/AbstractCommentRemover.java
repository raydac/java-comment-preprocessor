package com.igormaznitsa.jcp.removers;

import com.igormaznitsa.jcp.context.CommentRemoverType;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Objects;

public abstract class AbstractCommentRemover {
  protected final Reader srcReader;
  protected final Writer dstWriter;

  protected final boolean whiteSpaceAllowed;

  protected AbstractCommentRemover(final Reader src, final Writer dst,
                                   final boolean whiteSpaceAllowed) {
    Objects.requireNonNull(src, "The reader is null");
    Objects.requireNonNull(dst, "The writer is null");
    this.srcReader = src;
    this.dstWriter = dst;
    this.whiteSpaceAllowed = whiteSpaceAllowed;
  }

  public abstract Writer process() throws IOException;

  public static AbstractCommentRemover makeCommentRemover(
      final CommentRemoverType keepComments,
      final Reader src,
      final Writer dst,
      final boolean whiteSpaceAllowed) {
    switch (keepComments) {
      case KEEP_ALL: return new JustCopyRemover(src, dst, whiteSpaceAllowed);
      case REMOVE_C_STYLE: return new CStyleCommentRemover(src, dst, whiteSpaceAllowed);
      case REMOVE_JCP_ONLY: return new JcpCommentsRemover(src, dst, whiteSpaceAllowed);
      default: throw new IllegalStateException("Unsupported keep comments value: " + keepComments);
    }
  }

}
