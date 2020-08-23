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

import java.util.Objects;

public final class GetUtils {

  private GetUtils() {
  }

  /**
   * Get value and ensure that the value is not null
   *
   * @param <T>          type of value
   * @param value        the value
   * @param defaultValue the default value to be returned if the value is null
   * @return not null value
   * @throws AssertionError if both the value and the default value are null
   * @since 1.0
   */
  public static <T> T ensureNonNull(final T value, final T defaultValue) {
    return value == null ? Objects.requireNonNull(defaultValue) : value;
  }

  /**
   * Ensure that a string will not be null.
   *
   * @param value value to be checked
   * @return the value if it is not null or empty string if the value is null
   * @since 1.1.1
   */
  public static String ensureNonNullStr(final String value) {
    return value == null ? "" : value;
  }
}
