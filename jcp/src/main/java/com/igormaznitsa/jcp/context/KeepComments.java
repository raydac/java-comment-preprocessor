package com.igormaznitsa.jcp.context;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum KeepComments {
  KEEP_ALL,
  REMOVE_ALL,
  REMOVE_JCP;

  public static String makeStringForExpectedValues() {
    return Stream.concat(Stream.of("true", "false"),Arrays.stream(KeepComments.values())
        .map(Enum::toString)).collect(Collectors.joining(","));
  }

  public static KeepComments findForText(final String text) {
    KeepComments result = null;
    if (text != null && !text.isEmpty()) {
      final String normalized = text.trim().toUpperCase(Locale.ENGLISH);
      if (normalized.equals("TRUE")) {
        result = KEEP_ALL;
      } else if (normalized.equals("FALSE")) {
        result = REMOVE_ALL;
      } else {
        for (final KeepComments value : KeepComments.values()) {
          if (normalized.equals(value.name())) {
            result = value;
            break;
          }
        }
      }
    }
    if (result == null) {
      throw new IllegalArgumentException("Can't recognize keep comment value '" + text + "', allowed values: " + makeStringForExpectedValues());
    }
    return result;
  }
}
