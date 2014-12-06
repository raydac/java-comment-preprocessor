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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.io.*;

/**
 * It is an auxiliary class contains some useful methods
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public enum PreprocessorUtils {

  ;

    public static final String LINE_END;

  static {
    final String jcpLlineEnd = System.getProperty("jcp.line.separator");
    LINE_END = jcpLlineEnd == null ? System.getProperty("line.separator", "\r\n") : jcpLlineEnd;
  }

  public static String getFileExtension(final File file) {
    String result = null;
    if (file != null) {
      result = FilenameUtils.getExtension(file.getName());
    }
    return result;
  }

  public static String[] splitExtensionCommaList(final String extensions) {
    PreprocessorUtils.assertNotNull("String of extensions is null", extensions);

    final String trimmed = extensions.trim();

    String[] result;

    if (trimmed.isEmpty()) {
      result = new String[0];
    }
    else {
      result = splitForChar(extensions, ',');
      for (int li = 0; li < result.length; li++) {
        result[li] = result[li].trim().toLowerCase(Locale.ENGLISH);
      }
    }

    return result;
  }

  public static void assertNotNull(final String message, final Object obj) {
    if (obj == null) {
      throw new NullPointerException(message);
    }
  }

  public static BufferedReader makeFileReader(final File file, final String charset, final int bufferSize) throws IOException {
    PreprocessorUtils.assertNotNull("File is null", file);
    PreprocessorUtils.assertNotNull("Charset is null", charset);

    if (!Charset.isSupported(charset)) {
      throw new IllegalArgumentException("Unsupported charset [" + charset + ']');
    }

    BufferedReader result;

    if (bufferSize <= 0) {
      result = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
    }
    else {
      result = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset), bufferSize);
    }

    return result;
  }

  public static String[] replaceChar(final String[] source, final char toBeReplaced, final char replacement) {
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
    PreprocessorUtils.assertNotNull("Prefix is null", prefix);
    PreprocessorUtils.assertNotNull("Value is null", value);

    if (prefix.length() > value.length()) {
      throw new IllegalArgumentException("Prefix is taller than the value");
    }

    return value.substring(prefix.length());
  }

  public static void copyFile(final File source, final File dest) throws IOException {
    PreprocessorUtils.assertNotNull("Source is null", source);
    PreprocessorUtils.assertNotNull("Destination file is null", dest);

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
    }
    finally {
      IOUtils.closeQuietly(fileSrcInput);
      IOUtils.closeQuietly(fileOutput);
      IOUtils.closeQuietly(fileDst);
      IOUtils.closeQuietly(fileSrc);
    }
  }

  public static String processMacroses(final String processingString, final PreprocessorContext context) {
    int position;
    String result = processingString;

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
        }
        else {
          break;
        }
      }
      else {
        break;
      }
    }
    return result;
  }

  private static void checkFile(final File file) throws IOException {
    PreprocessorUtils.assertNotNull("File is null", file);

    if (!file.isFile()) {
      throw new FileNotFoundException("File " + getFilePath(file) + " doesn't exist");
    }
  }

  public static String[] readWholeTextFileIntoArray(final File file, final String encoding, final AtomicBoolean endedByNextLine) throws IOException {
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

        if (chr == '\n') {
          stringEndedByNextLine = true;
          strContainer.add(buffer.toString());
          buffer.setLength(0);
          meetCR = false;
        }
        else if (chr == '\r') {
          if (meetCR) {
            buffer.append((char) chr);
          }
          else {
            stringEndedByNextLine = false;
            meetCR = true;
          }
        }
        else {
          if (meetCR) {
            buffer.append('\r');
          }
          meetCR = false;
          stringEndedByNextLine = false;
          buffer.append((char) chr);
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
    finally {
      srcBufferedReader.close();
    }

    return strContainer.toArray(new String[strContainer.size()]);
  }

  public static byte[] readFileAsByteArray(final File file) throws IOException {
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
    }
    finally {
      IOUtils.closeQuietly(inChannel);
    }

    return buffer.array();
  }

  public static String[] splitForEqualChar(final String string) {
    final int index = string.indexOf('=');

    final String[] result;
    if (index < 0) {
      result = new String[]{string};
    }
    else {
      final String leftPart = string.substring(0, index).trim();
      final String rightPart = string.substring(index + 1).trim();
      result = new String[]{leftPart, rightPart};
    }
    return result;
  }

  public static String[] splitForChar(final String string, final char delimiter) {
    final char[] array = string.toCharArray();
    final StringBuilder buffer = new StringBuilder((array.length >> 1) == 0 ? 1 : array.length >> 1);

    final List<String> tokens = new ArrayList<String>(10);

    for (final char curChar : array) {
      if (curChar == delimiter) {
        if (buffer.length() != 0) {
          tokens.add(buffer.toString());
          buffer.setLength(0);
        }
      }
      else {
        buffer.append(curChar);
      }
    }

    if (buffer.length() != 0) {
      tokens.add(buffer.toString());
    }

    return tokens.toArray(new String[tokens.size()]);
  }

  public static String normalizeVariableName(final String name) {
    if (name == null) {
      return null;
    }

    return name.trim().toLowerCase(Locale.ENGLISH);
  }

  public static String getFilePath(final File file) {
    String result = "";
    if (file != null) {
      try {
        result = file.getCanonicalPath();
      }
      catch (IOException ex) {
        result = file.getAbsolutePath();
      }
    }
    return result;
  }

  public static void throwPreprocessorException(final String msg, final String processingString, final File srcFile, final int nextStringIndex, final Throwable cause) {
    throw new PreprocessorException(msg, processingString, new FilePositionInfo[]{new FilePositionInfo(srcFile, nextStringIndex)}, cause);
  }

  public static String[] replaceStringPrefix(final String[] allowedPrefixesToBeReplaced, final String replacement, final String[] strings) {
    final String[] result = new String[strings.length];

    for (int i = 0; i < strings.length; i++) {
      final String str = strings[i];

      String detectedPrefix = null;

      for (final String prefix : allowedPrefixesToBeReplaced) {
        if (str.startsWith(prefix)) {
          if (detectedPrefix == null || detectedPrefix.length() < prefix.length()) {
            detectedPrefix = prefix;
          }
        }
      }

      if (detectedPrefix != null) {
        result[i] = replacement + str.substring(detectedPrefix.length());
      }
      else {
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
}
