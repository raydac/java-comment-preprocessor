package com.igormaznitsa.jcp.removers;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class JustCopyRemover extends AbstractCommentRemover {

  public JustCopyRemover(final Reader src, final Writer dst,
                         final boolean whiteSpaceAllowed) {
    super(src, dst, whiteSpaceAllowed);
  }

  @Override
  public Writer process() throws IOException {
    final char [] buffer = new char[32768];
    while(!Thread.currentThread().isInterrupted()) {
      final int read = this.srcReader.read(buffer);
      if (read < 0) break;
      this.dstWriter.write(buffer, 0, read);
    }
    return this.dstWriter;
  }
}
