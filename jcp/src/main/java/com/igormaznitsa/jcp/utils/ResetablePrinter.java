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

package com.igormaznitsa.jcp.utils;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * The class implements a resetable char printer
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class ResetablePrinter {

  private final CharArrayWriter outStream;

  public ResetablePrinter(final int initialCapacity) {
    outStream = new CharArrayWriter(initialCapacity);
  }

  public boolean isEmpty() {
    return outStream.size() == 0;
  }

  public void writeBufferTo(final Writer writer) throws IOException {
    outStream.flush();
    writer.write(outStream.toCharArray());
    writer.flush();
  }

  public int getSize() {
    return outStream.size();
  }

  public void reset() {
    outStream.reset();
  }

  public void print(final String text) throws IOException {
    for (final char chr : text.toCharArray()) {
      outStream.write(chr);
    }
  }

  public void println(final String text, final String eol) throws IOException {
    for (final char chr : text.toCharArray()) {
      this.outStream.write(chr);
    }
    outStream.write(eol, 0, eol.length());
  }
}
