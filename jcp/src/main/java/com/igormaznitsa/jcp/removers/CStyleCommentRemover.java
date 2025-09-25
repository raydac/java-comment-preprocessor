/*
 * Copyright 2002-2019 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.igormaznitsa.jcp.removers;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * A remover allows to cut off all Java like comments from a reader and write the result into a writer
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class CStyleCommentRemover extends AbstractCommentRemover {

  public CStyleCommentRemover(final Reader src, final Writer dst,
                              final boolean whiteSpacesAllowed) {
    super(src, dst, whiteSpacesAllowed);
  }

  @Override
  public Writer process() throws IOException {
    final int STATE_NORMAL = 0;
    final int STATE_INSIDE_STRING = 1;
    final int STATE_NEXT_SPECIAL_CHAR = 2;
    final int STATE_FORWARD_SLASH = 3;

    int state = STATE_NORMAL;

    while (!Thread.currentThread().isInterrupted()) {
      final int chr = this.srcReader.read();
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
              skipTillClosingJavaComments();
              state = STATE_NORMAL;
            }
            break;
            case '/': {
              skipTillNextString();
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
            default:
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
        default:
          throw new IllegalStateException("Unexpected state: " + state);
      }
    }
    return dstWriter;
  }
}
