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

import java.lang.reflect.Array;

public final class ArrayUtils {

  public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
  public static final String[] EMPTY_STRING_ARRAY = new String[0];
  public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  public static final char[] EMPTY_CHAR_ARRAY = new char[0];
  public static final short[] EMPTY_SHORT_ARRAY = new short[0];
  public static final boolean[] EMPTY_BOOL_ARRAY = new boolean[0];
  public static final int[] EMPTY_INT_ARRAY = new int[0];
  public static final long[] EMPTY_LONG_ARRAY = new long[0];

  private ArrayUtils() {
  }

  /**
   * Join arrays provided as parameters, all arrays must be the same type, null values allowed.
   *
   * @param <T>    type of array
   * @param arrays array of arrays to be joined
   * @return all joined arrays as single array
   * @since 1.0
   */
  @SafeVarargs
  public static <T> T[] joinArrays(final T[]... arrays) {
    int commonLength = 0;
    for (final T[] array : arrays) {
      if (array != null) {
        commonLength += array.length;
      }
    }
    @SuppressWarnings("unchecked") final T[] result = (T[]) Array
        .newInstance(arrays.getClass().getComponentType().getComponentType(), commonLength);
    int position = 0;
    for (final T[] array : arrays) {
      if (array != null) {
        System.arraycopy(array, 0, result, position, array.length);
        position += array.length;
      }
    }
    return result;
  }

}
