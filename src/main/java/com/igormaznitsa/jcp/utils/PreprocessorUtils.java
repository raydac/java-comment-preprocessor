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
package com.igormaznitsa.jcp.utils;


import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.*;

import com.igormaznitsa.meta.annotation.MustNotContainNull;
import com.igormaznitsa.meta.annotation.ThrowsRuntimeException;
import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;
import com.igormaznitsa.meta.common.utils.Assertions;

/**
 * It is an auxiliary class contains some useful methods
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class PreprocessorUtils {

  private static final Pattern PATTERN_MACROS_WITH_SPACES = Pattern.compile("\\/\\*\\s*\\$(.*?)\\$\\s*\\*\\/");

  private PreprocessorUtils() {
  }

  public static final String LINE_END;

  static {
    final String jcpLlineEnd = System.getProperty("jcp.line.separator");
    LINE_END = jcpLlineEnd == null ? System.getProperty("line.separator", "\r\n") : jcpLlineEnd;
  }

  @Nullable
  public static String getFileExtension(@Nullable final File file) {
    String result = null;
    if (file != null) {
      result = FilenameUtils.getExtension(file.getName());
    }
    return result;
  }

  @Nonnull
  @MustNotContainNull
  @ThrowsRuntimeException(value = NullPointerException.class, reference = "if extensions are null")
  public static String[] splitExtensionCommaList(@Nonnull final String extensions) {
    assertNotNull("String of extensions is null", extensions);

    final String trimmed = extensions.trim();

    String[] result;

    if (trimmed.isEmpty()) {
      result = new String[0];
    } else {
      result = splitForChar(extensions, ',');
      for (int li = 0; li < result.length; li++) {
        result[li] = result[li].trim().toLowerCase(Locale.ENGLISH);
      }
    }

    return result;
  }

  @Nonnull
  public static BufferedReader makeFileReader(@Nonnull final File file, @Nonnull final String charset, final int bufferSize) throws IOException {
    assertNotNull("File is null", file);
    assertNotNull("Charset is null", charset);

    if (!Charset.isSupported(charset)) {
      throw new IllegalArgumentException("Unsupported charset [" + charset + ']');
    }

    BufferedReader result;

    if (bufferSize <= 0) {
      result = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
    } else {
      result = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset), bufferSize);
    }

    return result;
  }

  @Nonnull
  @MustNotContainNull
  public static String[] replaceChar(@Nonnull @MustNotContainNull final String[] source, final char toBeReplaced, final char replacement) {
    final String[] result = new String[source.length];
    int index = 0;
    for (final String curStr : source) {
      result[index++] = curStr.replace(toBeReplaced, replacement);
    }
    return result;
  }

  @Nonnull
  public static String extractTrimmedTail(@Nonnull final String prefix, @Nonnull final String value) {
    return extractTail(prefix, value).trim();
  }

  @Nonnull
  public static String extractTail(@Nonnull final String prefix, @Nonnull final String value) {
    assertNotNull("Prefix is null", prefix);
    assertNotNull("Value is null", value);

    if (prefix.length() > value.length()) {
      throw new IllegalArgumentException("Prefix is taller than the value");
    }

    return value.substring(prefix.length());
  }

  public static void copyFile(@Nonnull final File source, @Nonnull final File dest, final boolean copyFileAttributes) throws IOException {
    assertNotNull("Source is null", source);
    assertNotNull("Destination file is null", dest);

    if (source.isDirectory()) {
      throw new IllegalArgumentException("Source file is directory");
    }

    if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
      throw new IOException("Can't make directory [" + getFilePath(dest.getParentFile()) + ']');
    }

    FileChannel fileSrc = null;
    FileChannel fileDst = null;
    final FileInputStream fileSrcInput = new FileInputStream(source);
    FileOutputStream fileOutput = null;
    try {
      fileSrc = fileSrcInput.getChannel();
      fileOutput = new FileOutputStream(dest);
      fileDst = fileOutput.getChannel();
      long size = fileSrc.size();
      long pos = 0L;
      while (size > 0) {
        final long written = fileSrc.transferTo(pos, size, fileDst);
        pos += written;
        size -= written;
      }
    } finally {
      IOUtils.closeQuietly(fileSrcInput);
      IOUtils.closeQuietly(fileOutput);
      IOUtils.closeQuietly(fileDst);
      IOUtils.closeQuietly(fileSrc);
    }
    
      if (copyFileAttributes) {
        copyFileAttributes(source, dest);
      }
  }

  public static void copyFileAttributes(@Nonnull final File from, @Nonnull final File to) {
    to.setExecutable(from.canExecute());
    to.setReadable(from.canRead());
    to.setWritable(from.canWrite());
  }
  
  @Nonnull
  public static String replacePartByChar(@Nonnull final String text, final char chr, final int startPosition, final int length) {
    Assertions.assertTrue("Start position must be great or equals zero", startPosition >= 0);
    Assertions.assertTrue("Length must be great or equals zero", length >= 0);

    final StringBuilder result = new StringBuilder(text.length());

    result.append(text.subSequence(0, Math.min(text.length(), startPosition)));
    for (int i = startPosition; i < Math.min(text.length(), startPosition + length); i++) {
      result.append(chr);
    }
    result.append(text.subSequence(Math.min(startPosition + length, text.length()), text.length()));

    return result.toString();
  }
 
  @Nonnull
  public static String generateStringForChar(final char chr, final int length) {
    final StringBuilder buffer = new StringBuilder(Math.max(length,1));
    for (int i = 0; i < length; i++) {
      buffer.append(chr);
    }
    return buffer.toString();
  }
 
  @Nonnull
  public static String processMacroses(@Nonnull final String processingString, @Nonnull final PreprocessorContext context) {
    int position;
    String result = processingString;

    if (context.isAllowWhitespace()){
      final Matcher matcher = PATTERN_MACROS_WITH_SPACES.matcher(processingString);
      final StringBuilder buffer = new StringBuilder();
      int end = 0;
      while(matcher.find()){
        final int start = matcher.start();
        final int prevEnd = end;
        end = matcher.end();
        final String macrosBody = matcher.group(1);
        final Value value = Expression.evalExpression(macrosBody, context);
        buffer.append(processingString.substring(prevEnd,start));
        buffer.append(value.toString());
      }
      if (end<processingString.length()){
        buffer.append(processingString.substring(end));
      }
      result = buffer.toString();
    }else{
    while (true) {
        position = result.indexOf("/*$");

        if (position >= 0) {
          final String leftPart = result.substring(0, position);
          final int beginIndex = position;
          position = result.indexOf("$*/", position);
          if (position >= 0) {
            final String macrosBody = result.substring(beginIndex + 3, position);
            final String rightPart = result.substring(position + 3);

            final Value value = Expression.evalExpression(macrosBody, context);

            result = leftPart + value.toString() + rightPart;
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

  private static void checkFile(@Nonnull final File file) throws IOException {
    assertNotNull("File is null", file);

    if (!file.isFile()) {
      throw new FileNotFoundException("File " + getFilePath(file) + " doesn't exist");
    }
  }

  @Nonnull
  @MustNotContainNull
  public static String[] readWholeTextFileIntoArray(@Nonnull final File file, @Nullable final String encoding, @Nullable final AtomicBoolean endedByNextLine) throws IOException {
    checkFile(file);

    final String enc = encoding == null ? "UTF8" : encoding;

    final BufferedReader srcBufferedReader = PreprocessorUtils.makeFileReader(file, enc, (int) file.length());
    final List<String> strContainer = new ArrayList<String>(1024);
    try {
      final StringBuilder buffer = new StringBuilder();

      boolean stringEndedByNextLine = false;

      boolean meetCR = false;

      while (true) {
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
    } finally {
      srcBufferedReader.close();
    }

    return strContainer.toArray(new String[strContainer.size()]);
  }

  @Nonnull
  public static byte[] readFileAsByteArray(@Nonnull final File file) throws IOException {
    checkFile(file);

    int len = (int) file.length();

    final ByteBuffer buffer = ByteBuffer.allocate(len);
    final FileChannel inChannel = new FileInputStream(file).getChannel();

    try {
      while (len > 0) {
        final int read = inChannel.read(buffer);
        if (read < 0) {
          throw new IOException("Can't read whole file [" + getFilePath(file) + '\'');
        }
        len -= read;
      }
    } finally {
      IOUtils.closeQuietly(inChannel);
    }

    return buffer.array();
  }

  @Nonnull
  @MustNotContainNull
  public static String[] splitForEqualChar(@Nonnull final String string) {
    final int index = string.indexOf('=');

    final String[] result;
    if (index < 0) {
      result = new String[]{string};
    } else {
      final String leftPart = string.substring(0, index).trim();
      final String rightPart = string.substring(index + 1).trim();
      result = new String[]{leftPart, rightPart};
    }
    return result;
  }

  @Nonnull
  @MustNotContainNull
  public static String[] splitForCharAndHoldEmptyLine(@Nonnull final String string, final char delimiter) {
    String [] result = splitForChar(string, delimiter);
    return result.length == 0 ? new String[]{""} : result;
  }
  
  @Nonnull
  @MustNotContainNull
  public static String[] splitForChar(@Nonnull final String string, final char delimiter) {
    final char[] array = string.toCharArray();
    final StringBuilder buffer = new StringBuilder((array.length >> 1) == 0 ? 1 : array.length >> 1);

    final List<String> tokens = new ArrayList<String>(10);

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

    return tokens.toArray(new String[tokens.size()]);
  }

  @Nullable
  public static String normalizeVariableName(@Nullable final String name) {
    if (name == null) {
      return null;
    }

    return name.trim().toLowerCase(Locale.ENGLISH);
  }

  @Nonnull
  public static String getFilePath(@Nullable final File file) {
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

  public static void throwPreprocessorException(@Nullable final String msg, @Nullable final String processingString, @Nonnull final File srcFile, final int nextStringIndex, @Nullable final Throwable cause) {
    throw new PreprocessorException(msg, processingString, new FilePositionInfo[]{new FilePositionInfo(srcFile, nextStringIndex)}, cause);
  }

  @Nonnull
  @MustNotContainNull
  public static String[] replaceStringPrefix(@Nonnull @MustNotContainNull final String[] allowedPrefixesToBeReplaced, @Nonnull final String replacement, @Nonnull @MustNotContainNull final String[] strings) {
    final String[] result = new String[strings.length];

    for (int i = 0; i < strings.length; i++) {
      final String str = strings[i];

      String detectedPrefix = null;

      for (final String prefix : allowedPrefixesToBeReplaced) {
        if (str.startsWith(prefix) && (detectedPrefix == null || detectedPrefix.length() < prefix.length())) {
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

  @Nonnull
  public static String getNextLineCodes() {
    return System.getProperty("line.separator", "\r\n");
  }

  @Nonnull
  public static String leftTrim(@Nonnull String rawString) {
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

  public static boolean isFileContentEquals(@Nullable final File src, @Nullable final File dst) throws IOException {
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

    final int bufferSize = Math.min((int) src.length(), 65536);

    final byte[] srcBuffer = new byte[bufferSize];
    final byte[] dstBuffer = new byte[bufferSize];

    final InputStream srcIn = new BufferedInputStream(new FileInputStream(src), bufferSize);
    final InputStream dstIn = new BufferedInputStream(new FileInputStream(dst), bufferSize);
    try {
      while (true) {
        final int readSrc = IOUtils.read(srcIn, srcBuffer);
        final int readDst = IOUtils.read(dstIn, dstBuffer);

        if (readDst != readSrc) {
          return false;
        }
        if (readSrc == 0) {
          break;
        }

        if (!Arrays.equals(srcBuffer, dstBuffer)) {
          return false;
        }
      }
      return true;
    } finally {
      IOUtils.closeQuietly(srcIn);
      IOUtils.closeQuietly(dstIn);
    }
  }
}
