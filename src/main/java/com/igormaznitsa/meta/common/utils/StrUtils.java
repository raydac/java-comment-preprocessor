/*
 * Copyright 2016 Igor Maznitsa.
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
package com.igormaznitsa.meta.common.utils;


import javax.annotation.Nonnull;

import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

/**
 * Set of auxiliary string functions.
 * 
 * @since 1.1.2
 */
public final class StrUtils {
  private StrUtils () {
    
  }
  
  /**
   * Trim left white spaces in string.
   * @param value string to be trimmed
   * @return trimmed string
   * @since 1.1.2
   */
  @Nonnull
  public static String trimLeft(@Nonnull final String value) {
    final StringBuilder result = new StringBuilder(assertNotNull(value).length());
    int index = 0;
    for(;index<value.length();index++){
      final char chr = value.charAt(index);
      if (!(Character.isWhitespace(chr) || Character.isISOControl(chr))) break;
    }
    if (index<value.length()) {
      result.append(value, index, value.length());
    }
    return result.toString();
  }

  /**
   * Trim right white spaces in string.
   * @param value string to be trimmed
   * @return trimmed string
   */
  @Nonnull
  public static String trimRight(@Nonnull final String value) {
    final StringBuilder result = new StringBuilder(assertNotNull(value).length());
    int index = value.length()-1;
    for(;index>=0;index--){
      final char chr = value.charAt(index);
      if (!(Character.isWhitespace(chr) || Character.isISOControl(chr))) break;
    }
    if (index>0) {
      result.append(value, 0, index+1);
    }
    return result.toString();
  }
  
  /**
   * Remove all white space chars and ISO control chars.
   * @param value string to be processed
   * @return pressed string without white spaces and control chars
   * @since 1.1.2
   */
  @Nonnull
  public static String pressing(@Nonnull final String value) {
    final StringBuilder result = new StringBuilder(assertNotNull(value).length());
    for (int index = 0; index < value.length(); index++) {
      final char chr = value.charAt(index);
      if (Character.isWhitespace(chr) || Character.isISOControl(chr)) {
        continue;
      }
      result.append(chr);
    }
    return result.toString();
  }
}
