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
 * A remover allows to cut off all Java like comments contains JCP directives (single line comments started by '#' or '$')
 * from a reader and write the result into a writer.
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 * @since 7.1.0
 */
public class JcpCommentLineRemover extends AbstractCommentRemover {

  public JcpCommentLineRemover(final Reader src, final Writer dst,
                               final boolean whiteSpaceAllowed) {
    super(src, dst, whiteSpaceAllowed);
  }

  @Override
  public Writer process() throws IOException {
    final int STATE_NORMAL = 0;
    final int STATE_FORWARD_SLASH = 1;
    final int STATE_POSSIBLE_JCP = 2;

    final StringBuilder jcpBuffer = new StringBuilder();

    int state = STATE_NORMAL;

    while (!Thread.currentThread().isInterrupted()) {
      final int chr = this.srcReader.read();
      if (chr < 0) {
        break;
      }

      switch (state) {
        case STATE_NORMAL: {
          if (chr == '/') {
            state = STATE_FORWARD_SLASH;
          } else if (Character.isWhitespace(chr)) {
            this.dstWriter.write(chr);
          } else {
            this.dstWriter.write(chr);
            this.copyTillNextString();
          }
        }
        break;
        case STATE_POSSIBLE_JCP: {
          switch (chr) {
            case '$':
            case '#': {
              jcpBuffer.setLength(0);
              skipTillNextString();
              state = STATE_NORMAL;
            }
            break;
            default: {
              if (Character.isSpaceChar(chr) && this.whiteSpaceAllowed) {
                jcpBuffer.append((char) chr);
              } else {
                this.dstWriter.write(jcpBuffer.toString());
                this.dstWriter.write(chr);
                jcpBuffer.setLength(0);
                this.copyTillNextString();
                state = STATE_NORMAL;
              }
            }
          }
        }
        break;
        case STATE_FORWARD_SLASH: {
          switch (chr) {
            case '*': {
              this.dstWriter.write("/*");
              copyTillClosingJavaComments();
              state = STATE_NORMAL;
            }
            break;
            case '/': {
              jcpBuffer.append("//");
              state = STATE_POSSIBLE_JCP;
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
        default:
          throw new IllegalStateException("Unexpected state: " + state);
      }
    }
    if (jcpBuffer.length() > 0) {
      this.dstWriter.write(jcpBuffer.toString());
    }
    return this.dstWriter;
  }
}
