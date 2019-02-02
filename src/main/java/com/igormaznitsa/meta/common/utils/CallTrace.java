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

package com.igormaznitsa.meta.common.utils;

import com.igormaznitsa.meta.annotation.Weight;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * The Class allows to save stack trace history (it is possible to keep it in packed format) and restore it to text representation for request.
 *
 * @since 1.0
 */
@Weight(Weight.Unit.VARIABLE)
@ThreadSafe
@Immutable
public class CallTrace implements Serializable {

  /**
   * Default end-of-line for linux.
   *
   * @since 1.0
   */
  public static final String EOL_LINUX = "\n";
  /**
   * Default end-of-line for windows.
   *
   * @since 1.0
   */
  public static final String EOL_WINDOWS = "\r\n";
  private static final long serialVersionUID = -3908621401136825952L;
  private static final Charset UTF8 = Charset.forName("UTF-8");
  private final boolean packed;
  private final byte[] stacktrace;
  private final String threadDescriptor;
  private final String eol;

  /**
   * The Constructor allows to create call trace history point for the called method.
   *
   * @see #EOL_LINUX
   * @since 1.0
   */
  public CallTrace() {
    this(true, true, EOL_LINUX);
  }

  /**
   * The Constructor allows to create call trace history with defined end-of-line symbol and since needed stack item position.
   *
   * @param skipConstructors flag to skip first calls from constructors in the stack.
   * @param pack             flag shows that string data must be packed, false if should not be packed
   * @param eol              string shows which end-of-line should be used
   * @see #EOL_LINUX
   * @see #EOL_WINDOWS
   * @since 1.0.2
   */
  @Weight(value = Weight.Unit.VARIABLE, comment = "Depends on the call stack depth")
  public CallTrace(final boolean skipConstructors, final boolean pack, @Nonnull final String eol) {
    this.eol = eol;
    this.threadDescriptor = Thread.currentThread().toString();

    final StackTraceElement[] allElements = Thread.currentThread().getStackTrace();

    int index = 1;

    if (skipConstructors) {
      for (; index < allElements.length; index++) {
        if (!"<init>".equals(allElements[index].getMethodName())) {
          break;
        }
      }
    }
    final StringBuilder buffer = new StringBuilder((allElements.length - index) * 32);
    for (; index < allElements.length; index++) {
      if (buffer.length() > 0) {
        buffer.append(eol);
      }
      buffer.append(allElements[index].toString());
    }

    this.packed = pack;
    if (pack) {
      this.stacktrace = IOUtils.packData(buffer.toString().getBytes(UTF8));
    } else {
      this.stacktrace = buffer.toString().getBytes(UTF8);
    }
  }

  /**
   * Get the descriptor of the thread where the object instance was created.
   *
   * @return the descriptor as String
   * @see Thread#toString()
   * @since 1.0.2
   */
  @Nonnull
  public String getThreadDescriptor() {
    return this.threadDescriptor;
  }

  /**
   * Restore stack trace as a string from inside data representation.
   *
   * @return the stack trace as String
   */
  @Nonnull
  public String restoreStackTrace() {
    return "THREAD_ID : " + this.threadDescriptor + this.eol + new String(this.packed ? IOUtils.unpackData(this.stacktrace) : this.stacktrace, UTF8);
  }

  @Override
  public String toString() {
    return restoreStackTrace();
  }
}
