package com.igormaznitsa.jcp.removers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public abstract class AbstractCommentRemoverTest {
  protected final boolean whiteSpaced;

  public AbstractCommentRemoverTest(final boolean whiteSpaced) {
    this.whiteSpaced = whiteSpaced;
  }

  @Parameterized.Parameters
  public static Collection<Boolean> data() {
    return Arrays.asList(Boolean.FALSE, Boolean.TRUE);
  }

  protected abstract AbstractCommentRemover makeCommentRemoverInstance(Reader reader, Writer writer,
                                                                       boolean whiteSpaceAllowed);

  public void assertCommentRemove(final String source, final String expected) {
    final Reader sourceReader = new StringReader(source);
    final Writer writer = new StringWriter();
    final AbstractCommentRemover remover =
        this.makeCommentRemoverInstance(sourceReader, writer, this.whiteSpaced);
    try (Writer resultWriter = remover.process()) {
      assertEquals(expected, resultWriter.toString());
    } catch (IOException ex) {
      fail("Unexpected error: " + ex.getMessage());
    }
  }
}
