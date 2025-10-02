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

import static com.igormaznitsa.jcp.context.CommentRemoverType.KEEP_ALL;
import static com.igormaznitsa.jcp.context.CommentRemoverType.REMOVE_C_STYLE;
import static com.igormaznitsa.jcp.context.CommentRemoverType.makeListOfAllRemoverIds;

import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.containers.TextFileDataContainer;
import com.igormaznitsa.jcp.context.CommentRemoverType;
import com.igormaznitsa.jcp.context.CommentTextProcessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.context.SpecialVariableProcessor;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * It is an auxiliary class contains some useful methods
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class PreprocessorUtils {

  /**
   * Property if defined and true then search of services in class path is disabled.
   *
   * @since 7.2.0
   */
  public static final String SYSTEM_PROPERTY_DISABLE_SEARCH_SERVICES =
      "jcp.preprocessor.disable.search.services";
  private static final Pattern PATTERN_MACROS_WITH_SPACES =
      Pattern.compile("\\/\\*\\s*\\$(.*?)\\$\\s*\\*\\/");

  private PreprocessorUtils() {
  }

  /**
   * Find active FileInfoContainer in a context.
   *
   * @param context preprocessor context, must not be null
   * @return found FileInfoContainer or empty optional
   * @see FileInfoContainer
   * @since 7.3.0
   */
  public static Optional<FileInfoContainer> findActiveFileInfoContainer(
      final PreprocessorContext context
  ) {
    final Optional<FileInfoContainer> result = context.getPreprocessingState()
        .getIncludeStack()
        .stream()
        .findFirst()
        .map(TextFileDataContainer::getFile)
        .flatMap(context::findFileInfoContainer);
    return result.isPresent() ? result :
        Optional.ofNullable(context.getPreprocessingState().getRootFileInfo());
  }

  /**
   * Find current file position info in a context.
   *
   * @param context preprocessor context, must not be null
   * @return found current file position info, must not be null
   * @since 7.3.0
   * @throws PreprocessorException thrown if it is impossible to find position info.
   */
  public static FilePositionInfo extractFilePositionInfo(
      final PreprocessorContext context
  ) {
    return context.getPreprocessingState().findFilePositionInfo()
        .orElseThrow(() -> context.makeException("Can't find position info in the context", null));
  }

  /**
   * Find all services in class path and register them in provided context.
   *
   * @param context target context must not be null
   * @since 7.3.0
   */
  public static void fillContextByFoundServices(final PreprocessorContext context) {
    final List<CommentTextProcessor> commentTextProcessors = findAndInstantiateAllServices(
        CommentTextProcessor.class);
    if (!commentTextProcessors.isEmpty()) {
      context.getPreprocessorLogger()
          .info(String.format("Detected %d comment text processing service(s): %s",
              commentTextProcessors.size(),
              commentTextProcessors.stream().map(x -> x.getClass().getCanonicalName())
                  .collect(Collectors.joining(","))));
      commentTextProcessors.forEach(context::addCommentTextProcessor);
    }

    final List<SpecialVariableProcessor> specialVariableProcessors = findAndInstantiateAllServices(
        SpecialVariableProcessor.class);
    if (!specialVariableProcessors.isEmpty()) {
      context.getPreprocessorLogger()
          .info(String.format("Detected %d special variable service(s): %s",
              specialVariableProcessors.size(),
              specialVariableProcessors.stream().map(x -> x.getClass().getCanonicalName())
                  .collect(Collectors.joining(","))));
      specialVariableProcessors.forEach(context::registerSpecialVariableProcessor);
    }

    final List<PreprocessorExtension> preprocessorExtensions = findAndInstantiateAllServices(
        PreprocessorExtension.class);
    if (!preprocessorExtensions.isEmpty()) {
      context.getPreprocessorLogger()
          .info(String.format("Detected %d preprocessor extension service(s): %s",
              preprocessorExtensions.size(),
              preprocessorExtensions.stream().map(x -> x.getClass().getCanonicalName())
                  .collect(Collectors.joining(","))));
      preprocessorExtensions.forEach(context::addPreprocessorExtension);
    }
  }

  /**
   * Find and instantiate a preprocessor extension for its class name. <b>Class must have default constructor.</b>
   * The preprocessor extension will be called for action directives.
   *
   * @param className preprocessor extension class name, can be null.
   * @return found and instantiated preprocessor extension or null if class name is null
   * @throws RuntimeException with cause exception if any error during method call.
   * @since 7.1.2
   */
  public static PreprocessorExtension findAndInstantiatePreprocessorExtensionForClassName(
      final String className) {
    if (className == null) {
      return null;
    }
    try {
      final Class<?> foundClass = Class.forName(className);
      return (PreprocessorExtension) foundClass.getConstructor().newInstance();
    } catch (Exception ex) {
      throw new RuntimeException("Can't instantiate preprocessor extension class: " + className,
          ex);
    }
  }

  /**
   * Find comment remover type for provided identifier. Decoding also true and false values.
   *
   * @param text comment remover id as string, must not be null.
   * @return found appropriate comment remover type, must not be null
   * @since 7.1.0
   */
  public static CommentRemoverType findCommentRemoverForId(final String text) {
    CommentRemoverType result = null;
    if (text != null && !text.isEmpty()) {
      final String normalized = text.trim().toUpperCase(Locale.ROOT);
      if (normalized.equals("TRUE")) {
        result = KEEP_ALL;
      } else if (normalized.equals("FALSE")) {
        result = REMOVE_C_STYLE;
      } else {
        for (final CommentRemoverType value : CommentRemoverType.values()) {
          if (normalized.equals(value.name())) {
            result = value;
            break;
          }
        }
      }
    }
    if (result == null) {
      throw new IllegalArgumentException(
          "Can't recognize keep comment value '" + text + "' (allowed values: true,false," +
              makeListOfAllRemoverIds() + ')');
    }
    return result;
  }

  public static String getFileExtension(final File file) {
    String result = null;
    if (file != null) {
      result = FilenameUtils.getExtension(file.getName());
    }
    return result;
  }

  public static BufferedReader makeFileReader(final File file, final Charset charset,
                                              final int bufferSize) throws IOException {
    Objects.requireNonNull(file, "File is null");
    Objects.requireNonNull(charset, "Charset is null");

    BufferedReader result;

    if (bufferSize <= 0) {
      result = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
    } else {
      result =
          new BufferedReader(new InputStreamReader(new FileInputStream(file), charset), bufferSize);
    }

    return result;
  }

  public static String[] replaceChar(final String[] source,
                                     final char toBeReplaced, final char replacement) {
    final String[] result = new String[source.length];
    int index = 0;
    for (final String curStr : source) {
      result[index++] = curStr.replace(toBeReplaced, replacement);
    }
    return result;
  }

  public static String extractTrimmedTail(final String prefix, final String value) {
    return extractTail(prefix, value).trim();
  }

  public static String extractTail(final String prefix, final String value) {
    Objects.requireNonNull(prefix, "Prefix is null");
    Objects.requireNonNull(value, "Value is null");

    if (prefix.length() > value.length()) {
      throw new IllegalArgumentException("Prefix is too long");
    }

    return value.substring(prefix.length());
  }

  public static void copyFile(final File source, final File dest, final boolean copyFileAttributes)
      throws IOException {
    Objects.requireNonNull(source, "Source is null");
    Objects.requireNonNull(dest, "Destination file is null");

    if (source.isDirectory()) {
      throw new IllegalArgumentException("Source file is directory");
    }

    if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
      throw new IOException("Can't make directory [" + getFilePath(dest.getParentFile()) + ']');
    }

    FileUtils.copyFile(source, dest);
    if (copyFileAttributes) {
      copyFileAttributes(source, dest);
    }
  }

  public static boolean copyFileAttributes(final File from, final File to) {
    boolean result = to.setExecutable(from.canExecute());
    result = result && to.setReadable(from.canRead());
    result = result && to.setWritable(from.canWrite());
    result = result && to.setLastModified(from.lastModified());
    return result;
  }

  public static String replacePartByChar(final String text, final char chr, final int startPosition,
                                         final int length) {
    if (startPosition < 0) {
      throw new IllegalArgumentException("Start position must be great or equal zero");
    }
    if (length < 0) {
      throw new IllegalArgumentException("Length must be great or equal zero");
    }

    final StringBuilder result = new StringBuilder(text.length());

    result.append(text.subSequence(0, Math.min(text.length(), startPosition)));
    result.append(String.valueOf(chr)
        .repeat(Math.max(0, Math.min(text.length(), startPosition + length) - startPosition)));
    result.append(text.subSequence(Math.min(startPosition + length, text.length()), text.length()));

    return result.toString();
  }

  public static <T> List<T> findAndInstantiateAllServices(final Class<T> serviceClass) {
    if (Boolean.getBoolean(SYSTEM_PROPERTY_DISABLE_SEARCH_SERVICES)) {
      return List.of();
    }
    final ServiceLoader<T> serviceLoader = ServiceLoader.load(serviceClass);
    return serviceLoader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
  }

  public static String generateStringForChar(final char chr, final int length) {
    final StringBuilder buffer = new StringBuilder(Math.max(length, 1));
    buffer.append(String.valueOf(chr).repeat(Math.max(0, length)));
    return buffer.toString();
  }


  public static String processMacroses(final String processingString,
                                       final PreprocessorContext context) {
    int position;
    String result = processingString;

    if (context.isAllowWhitespaces()) {
      final Matcher matcher = PATTERN_MACROS_WITH_SPACES.matcher(processingString);
      final StringBuilder buffer = new StringBuilder();
      int end = 0;
      while (matcher.find()) {
        final int start = matcher.start();
        final int prevEnd = end;
        end = matcher.end();
        final String macrosBody = matcher.group(1);
        final Value value = Expression.evalExpression(macrosBody, context);
        buffer.append(processingString, prevEnd, start);
        buffer.append(value);
      }
      if (end < processingString.length()) {
        buffer.append(processingString.substring(end));
      }
      result = buffer.toString();
    } else {
      while (!Thread.currentThread().isInterrupted()) {
        position = result.indexOf("/*$");

        if (position >= 0) {
          final String leftPart = result.substring(0, position);
          final int beginIndex = position;
          position = result.indexOf("$*/", position);
          if (position >= 0) {
            final String macrosBody = result.substring(beginIndex + 3, position);
            final String rightPart = result.substring(position + 3);

            final Value value = Expression.evalExpression(macrosBody, context);

            result = leftPart + value + rightPart;
          } else {
            break;
          }
        } else {
          break;
        }
      }
    }
    return result;
  }

  private static void checkFile(final File file) throws IOException {
    Objects.requireNonNull(file, "File is null");

    if (!file.isFile()) {
      throw new FileNotFoundException("File " + getFilePath(file) + " doesn't exist");
    }
  }

  public static String[] readWholeTextFileIntoArray(final File file, final Charset encoding,
                                                    final AtomicBoolean endedByNextLine)
      throws IOException {
    checkFile(file);

    final List<String> strContainer = new ArrayList<>(1024);
    try (BufferedReader srcBufferedReader = PreprocessorUtils
        .makeFileReader(file, encoding == null ? StandardCharsets.UTF_8 : encoding,
            (int) file.length())) {
      final StringBuilder buffer = new StringBuilder();

      boolean stringEndedByNextLine = false;

      boolean meetCR = false;

      while (!Thread.currentThread().isInterrupted()) {
        final int chr = srcBufferedReader.read();
        if (chr < 0) {
          break;
        }

        switch (chr) {
          case '\n': {
            stringEndedByNextLine = true;
            strContainer.add(buffer.toString());
            buffer.setLength(0);
            meetCR = false;
          }
          break;
          case '\r': {
            if (meetCR) {
              buffer.append((char) chr);
            } else {
              stringEndedByNextLine = false;
              meetCR = true;
            }
          }
          break;
          default: {
            if (meetCR) {
              buffer.append('\r');
            }
            meetCR = false;
            stringEndedByNextLine = false;
            buffer.append((char) chr);
          }
          break;
        }
      }

      if (buffer.length() != 0) {
        strContainer.add(buffer.toString());
        buffer.setLength(0);
      }

      if (endedByNextLine != null) {
        endedByNextLine.set(stringEndedByNextLine);
      }
    }

    return strContainer.toArray(new String[0]);
  }

  public static String[] splitForEqualChar(final String string) {
    final int index = string.indexOf('=');

    final String[] result;
    if (index < 0) {
      result = new String[] {string};
    } else {
      final String leftPart = string.substring(0, index).trim();
      final String rightPart = string.substring(index + 1).trim();
      result = new String[] {leftPart, rightPart};
    }
    return result;
  }


  public static List<String> splitForCharAndHoldEmptyLine(final String string,
                                                          final char delimiter) {
    final List<String> result = splitForChar(string, delimiter);
    if (result.isEmpty()) {
      result.add("");
    }
    return result;
  }

  public static List<String> splitForChar(final String string, final char delimiter) {
    final char[] array = string.toCharArray();
    final StringBuilder buffer =
        new StringBuilder((array.length >> 1) == 0 ? 1 : array.length >> 1);

    final List<String> tokens = new ArrayList<>(10);

    for (final char curChar : array) {
      if (curChar == delimiter) {
        if (buffer.length() != 0) {
          tokens.add(buffer.toString());
          buffer.setLength(0);
        }
      } else {
        buffer.append(curChar);
      }
    }

    if (buffer.length() != 0) {
      tokens.add(buffer.toString());
    }

    return tokens;
  }


  public static String normalizeVariableName(final String name) {
    if (name == null) {
      return null;
    }

    return name.trim().toLowerCase(Locale.ROOT);
  }


  public static String getFilePath(final File file) {
    String result = "";
    if (file != null) {
      try {
        result = file.getCanonicalPath();
      } catch (IOException ex) {
        result = file.getAbsolutePath();
      }
    }
    return result;
  }

  public static void throwPreprocessorException(final String msg, final String processingString,
                                                final File srcFile, final int nextStringIndex,
                                                final Throwable cause) {
    throw new PreprocessorException(msg, processingString,
        new FilePositionInfo[] {new FilePositionInfo(srcFile, nextStringIndex)}, cause);
  }

  public static String[] replaceStringPrefix(
      final String[] allowedPrefixesToBeReplaced, final String replacement,
      final String[] strings) {
    final String[] result = new String[strings.length];

    for (int i = 0; i < strings.length; i++) {
      final String str = strings[i];

      String detectedPrefix = null;

      for (final String prefix : allowedPrefixesToBeReplaced) {
        if (str.startsWith(prefix) &&
            (detectedPrefix == null || detectedPrefix.length() < prefix.length())) {
          detectedPrefix = prefix;
        }
      }

      if (detectedPrefix != null) {
        result[i] = replacement + str.substring(detectedPrefix.length());
      } else {
        result[i] = str;
      }
    }

    return result;
  }


  public static String getNextLineCodes() {
    return System.getProperty("line.separator", "\r\n");
  }


  public static String leftTrim(String rawString) {
    int firstNonSpace = 0;
    for (int i = 0; i < rawString.length(); i++) {
      final char ch = rawString.charAt(i);
      if (ch > 32) {
        break;
      }
      firstNonSpace++;
    }
    return rawString.substring(firstNonSpace);
  }

  public static boolean isFileContentEquals(final File src, final File dst) throws IOException {
    if (src == null && dst == null) {
      return true;
    }
    if (src == null || dst == null) {
      return false;
    }
    if (src.isDirectory() && dst.isDirectory()) {
      return true;
    }
    if (src.isDirectory() || dst.isDirectory()) {
      return false;
    }

    if (src.length() != dst.length()) {
      return false;
    }
    return FileUtils.contentEquals(src, dst);
  }
}
