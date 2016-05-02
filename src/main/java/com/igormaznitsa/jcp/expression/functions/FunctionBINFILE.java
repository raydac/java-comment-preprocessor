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
package com.igormaznitsa.jcp.expression.functions;

import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;

import java.io.*;

import java.util.Locale;
import javax.annotation.Nonnull;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.igormaznitsa.meta.annotation.MustNotContainNull;

/**
 * The Function loads bin file and encodes it into string.
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 * @since 6.1.0
 */
public class FunctionBINFILE extends AbstractFunction {

  private static final ValueType[][] ARG_TYPES = new ValueType[][]{{ValueType.STRING, ValueType.STRING}};

  @Override
  @Nonnull
  public String getName() {
    return "binfile";
  }

  @Override
  @Nonnull
  public String getReference() {
    return "encode bin file into string representation";
  }

  @Override
  public int getArity() {
    return 2;
  }

  @Override
  @Nonnull
  @MustNotContainNull
  public ValueType[][] getAllowedArgumentTypes() {
    return ARG_TYPES;
  }

  @Override
  @Nonnull
  public ValueType getResultType() {
    return ValueType.STRING;
  }

  @Nonnull
  public Value executeStrStr(@Nonnull final PreprocessorContext context, @Nonnull final Value strfilePath, @Nonnull final Value encodeType) {
    final String filePath = strfilePath.asString();
    final String encode = encodeType.asString().trim().toLowerCase(Locale.ENGLISH);

    final File theFile;
    try {
      theFile = context.findFileInSourceFolder(filePath);
    } catch (IOException ex) {
      throw context.makeException("Can't find bin file '" + filePath + '\'', null);
    }

    if (context.isVerbose()) {
      context.logForVerbose("Loading content of bin file '" + theFile + '\'');
    }

    try {
      final String endOfLine = System.getProperty("line.separator","\r\n");
      final String result;
      if ("base64".equals(encode)) {
        result = convertToBase64(theFile, -1, endOfLine);
      } else if ("base64s".equals(encode)) {
          result = convertToBase64(theFile, 80, endOfLine);
        } else if ("byte[]".equals(encode)) {
          result = convertToJBytes(theFile, -1, endOfLine);
        } else if ("byte[]s".equals(encode)) {
          result = convertToJBytes(theFile, 80, endOfLine);
        }else {
          throw context.makeException("Unsupported encode type [" + encode + ']', null);
        }
      
      return Value.valueOf(result);
    } catch (Exception ex) {
      throw context.makeException("Unexpected exception", ex);
    }
  }

  @Nonnull
  private String convertToJBytes(@Nonnull final File file, final int lineLength, @Nonnull final String endOfLine) throws IOException {
    final StringBuilder result = new StringBuilder(512);
    final byte [] array = FileUtils.readFileToByteArray(file);
    
    int endLinePos = lineLength;
    
    for(final byte b : array) {
      if (result.length()>0) result.append(',');
      result.append("(byte)0x").append(Integer.toHexString(b & 0xFF).toUpperCase(Locale.ENGLISH));
      if (lineLength>0 && result.length()>=endLinePos){
        result.append(endOfLine);
        endLinePos = result.length()+lineLength;
      }
    }
    
    return result.toString();
  }
  
  @Nonnull
  private String convertToBase64(@Nonnull final File file, final int lineLength, @Nonnull final String lineSeparator) throws IOException {
    return new Base64(lineLength, lineSeparator.getBytes("UTF-8"), false).encodeAsString(FileUtils.readFileToByteArray(file));
  }
}
