/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.jcp.removers;

import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import javax.annotation.Nonnull;

/**
 * A remover allows to cut off all Java like comments from a reader and write
 * the result into a writer
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class JavaCommentsRemover {

  private final Reader srcReader;
  private final Writer dstWriter;

  public JavaCommentsRemover(@Nonnull final Reader src, @Nonnull final Writer dst) {
    assertNotNull("The reader is null", src);
    assertNotNull("The writer is null", dst);
    this.srcReader = src;
    this.dstWriter = dst;
  }

  void skipUntilNextString() throws IOException {
    while (true) {
      final int chr = srcReader.read();
      if (chr < 0) {
        return;
      }

      if (chr == '\n') {
        dstWriter.write(chr);
        return;
      }
    }
  }

  void skipUntilClosingComments() throws IOException {
    boolean starFound = false;

    while (true) {
      final int chr = srcReader.read();
      if (chr < 0) {
        return;
      }
      if (starFound) {
        if (chr == '/') {
          return;
        }
        else {
          starFound = chr == '*';
        }
      }
      else {
        if (chr == '*') {
          starFound = true;
        }
      }
    }
  }

  @Nonnull
  public Writer process() throws IOException {
    final int STATE_NORMAL = 0;
    final int STATE_INSIDE_STRING = 1;
    final int STATE_NEXT_SPECIAL_CHAR = 2;
    final int STATE_FORWARD_SLASH = 3;

    int state = STATE_NORMAL;

    while (true) {
      final int chr = srcReader.read();
      if (chr < 0) {
        break;
      }

      switch (state) {
        case STATE_NORMAL: {
          switch (chr) {
            case '\"': {
              dstWriter.write(chr);
              state = STATE_INSIDE_STRING;
            }
            break;
            case '/': {
              state = STATE_FORWARD_SLASH;
            }
            break;
            default: {
              dstWriter.write(chr);
            }
            break;
          }
        }
        break;
        case STATE_FORWARD_SLASH: {
          switch (chr) {
            case '*': {
              skipUntilClosingComments();
              state = STATE_NORMAL;
            }
            break;
            case '/': {
              skipUntilNextString();
              state = STATE_NORMAL;
            }
            break;
            default: {
              dstWriter.write('/');
              dstWriter.write(chr);
              state = STATE_NORMAL;
            }
            break;
          }
        }
        break;
        case STATE_INSIDE_STRING: {
          switch (chr) {
            case '\\': {
              state = STATE_NEXT_SPECIAL_CHAR;
            }
            break;
            case '\"': {
              state = STATE_NORMAL;
            }
            break;
          }
          dstWriter.write(chr);
        }
        break;
        case STATE_NEXT_SPECIAL_CHAR: {
          dstWriter.write(chr);
          state = STATE_INSIDE_STRING;
        }
        break;
      }
    }
    return dstWriter;
  }
}
