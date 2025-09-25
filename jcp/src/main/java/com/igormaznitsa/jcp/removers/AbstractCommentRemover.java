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

  public static AbstractCommentRemover makeCommentRemover(
      final CommentRemoverType keepComments,
      final Reader src,
      final Writer dst,
      final boolean whiteSpaceAllowed) {
    switch (keepComments) {
      case KEEP_ALL:
        return new JustCopyRemover(src, dst, whiteSpaceAllowed);
      case REMOVE_C_STYLE:
        return new CStyleCommentRemover(src, dst, whiteSpaceAllowed);
      case REMOVE_JCP_ONLY:
        return new JcpCommentLineRemover(src, dst, whiteSpaceAllowed);
      default:
        throw new IllegalStateException("Unsupported keep comments value: " + keepComments);
    }
  }

  public abstract Writer process() throws IOException;

  protected void skipTillNextString() throws IOException {
    while (!Thread.currentThread().isInterrupted()) {
      final int chr = srcReader.read();
      if (chr < 0) {
        return;
      }

      if (chr == '\n') {
        this.dstWriter.write(chr);
        return;
      }
    }
  }

  protected void skipTillClosingJavaComments() throws IOException {
    boolean starFound = false;

    while (!Thread.currentThread().isInterrupted()) {
      final int chr = srcReader.read();
      if (chr < 0) {
        return;
      }
      if (starFound) {
        if (chr == '/') {
          return;
        } else {
          starFound = chr == '*';
        }
      } else if (chr == '*') {
        starFound = true;
      }
    }
  }

  protected void copyTillClosingJavaComments() throws IOException {
    boolean starFound = false;

    while (!Thread.currentThread().isInterrupted()) {
      final int chr = this.srcReader.read();
      if (chr < 0) {
        return;
      }
      this.dstWriter.write(chr);
      if (starFound) {
        if (chr == '/') {
          return;
        } else {
          starFound = chr == '*';
        }
      } else if (chr == '*') {
        starFound = true;
      }
    }
  }

  protected void copyTillNextString() throws IOException {
    while (!Thread.currentThread().isInterrupted()) {
      final int chr = srcReader.read();
      if (chr < 0) {
        return;
      } else {
        this.dstWriter.write(chr);
        if (chr == '\n') {
          break;
        }
      }
    }
  }

}
