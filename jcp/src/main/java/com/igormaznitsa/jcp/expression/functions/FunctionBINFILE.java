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

package com.igormaznitsa.jcp.expression.functions;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.Deflater;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

/**
 * The Function loads bin file and encodes it into string.
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 * @since 6.1.0
 */
public class FunctionBINFILE extends AbstractFunction {

  private static final List<List<ValueType>> ARG_TYPES =
      List.of(List.of(ValueType.STRING, ValueType.STRING));

  private static boolean hasSplitFlag(final String name, final Type type) {
    final String opts = name.substring(type.name.length());
    return opts.contains("S") || opts.contains("s");
  }

  private static boolean hasDeflateFlag(final String name, final Type type) {
    final String opts = name.substring(type.name.length());
    return opts.contains("D") || opts.contains("d");
  }


  private static String convertTo(final File file, final Type type, final boolean deflate,
                                  final int lineLength, final String endOfLine) throws IOException {
    final StringBuilder result = new StringBuilder(512);
    byte[] array = FileUtils.readFileToByteArray(file);

    if (deflate) {
      array = deflate(array);
    }

    boolean addNextLine = false;

    int visibleLineCharsCounter = 0;

    switch (type) {
      case BASE64: {
        final String baseEncoded =
            new Base64(lineLength, endOfLine.getBytes(StandardCharsets.UTF_8), false)
                .encodeAsString(array);
        result.append(baseEncoded.trim());
      }
      break;
      case BYTEARRAY:
      case INT8:
      case UINT8: {
        for (final byte b : array) {
          if (result.length() > 0) {
            result.append(',');
            visibleLineCharsCounter++;
          }

          if (addNextLine) {
            addNextLine = false;
            visibleLineCharsCounter = 0;
            result.append(endOfLine);
          }

          final int initialBufferLength = result.length();
          switch (type) {
            case BYTEARRAY: {
              result.append("(byte)0x")
                  .append(Integer.toHexString(b & 0xFF).toUpperCase(Locale.ROOT));
            }
            break;
            case UINT8: {
              result.append(Integer.toString(b & 0xFF).toUpperCase(Locale.ROOT));
            }
            break;
            case INT8: {
              result.append(Integer.toString(b).toUpperCase(Locale.ROOT));
            }
            break;
            default:
              throw new Error("Unexpected type : " + type);
          }
          visibleLineCharsCounter += result.length() - initialBufferLength;

          if (lineLength > 0 && visibleLineCharsCounter >= lineLength) {
            addNextLine = true;
          }
        }
      }
      break;
      default:
        throw new Error("Unexpected type : " + type);
    }

    return result.toString();
  }


  private static byte[] deflate(final byte[] data) throws IOException {
    final Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
    deflater.setInput(data);

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

    deflater.finish();
    final byte[] buffer = new byte[1024];
    while (!deflater.finished()) {
      final int count = deflater.deflate(buffer);
      outputStream.write(buffer, 0, count);
    }
    outputStream.close();
    final byte[] output = outputStream.toByteArray();

    deflater.end();

    return output;
  }

  @Override
  public String getName() {
    return "binfile";
  }

  @Override
  public String getReference() {
    final StringBuilder buffer = new StringBuilder();
    for (final Type t : Type.values()) {
      if (buffer.length() > 0) {
        buffer.append('|');
      }
      buffer.append(t.name);
    }
    buffer.append("[s|d|sd|ds]");
    return "encode binary file as string, allowed types [" + buffer +
        "], s - split lines, d - deflate compression";
  }

  @Override
  public Set<Integer> getArity() {
    return ARITY_2;
  }

  @Override
  public List<List<ValueType>> getAllowedArgumentTypes() {
    return ARG_TYPES;
  }

  @Override
  public ValueType getResultType() {
    return ValueType.STRING;
  }


  public Value executeStrStr(final PreprocessorContext context, final Value strFilePath,
                             final Value encodeType) {
    final String filePath = strFilePath.asString();
    final String encodeTypeAsString = encodeType.asString();
    final Type type = Type.find(encodeTypeAsString);

    if (type == null) {
      throw context.makeException("Unsupported encode type [" + encodeType.asString() + ']', null);
    }

    final int lengthOfLine = hasSplitFlag(encodeTypeAsString, type) ? 80 : -1;
    final boolean doDeflate = hasDeflateFlag(encodeTypeAsString, type);

    final File theFile = context.findFileInSources(filePath);

    if (context.isVerbose()) {
      context.logForVerbose("Loading content of bin file '" + theFile + '\'');
    }

    try {
      final String endOfLine = System.getProperty("line.separator", "\r\n");
      PreprocessorUtils.findActiveFileInfoContainer(context)
          .ifPresent(t ->
              t.getIncludedSources().add(theFile));
      return Value.valueOf(convertTo(theFile, type, doDeflate, lengthOfLine, endOfLine));
    } catch (Exception ex) {
      throw context.makeException("Unexpected exception", ex);
    }
  }

  private enum Type {
    BASE64("base64"),
    BYTEARRAY("byte[]"),
    UINT8("uint8[]"),
    INT8("int8[]");

    private final String name;

    Type(final String name) {
      this.name = name;
    }


    public static Type find(final String name) {
      Type result = null;
      if (name != null) {
        final String normalized = name.toLowerCase(Locale.ROOT).trim();
        for (final Type t : values()) {
          if (normalized.startsWith(t.name)) {
            result = t;
            break;
          }
        }
      }
      return result;
    }


    public String getName() {
      return this.name;
    }
  }
}
