/*
 * Copyright 2011 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307  USA
 */
package com.igormaznitsa.jcpreprocessor.utils;

import com.igormaznitsa.jcpreprocessor.context.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.exceptions.FilePositionInfo;
import com.igormaznitsa.jcpreprocessor.exceptions.PreprocessorException;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import java.io.BufferedReader;
import java.io.Closeable;
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

/**
 * It is an auxiliary class contains some useful methods
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public enum PreprocessorUtils {

    ;

    public static String getFileExtension(final File file) {
        String result = null;
        if (file != null) {

            final String fileName = file.getName();
            final int lastPointPos = fileName.lastIndexOf('.');
            if (lastPointPos < 0) {
                result = "";
            } else {
                result = fileName.substring(lastPointPos + 1);
            }
        }
        return result;
    }

    public static String[] extractExtensions(final String extensions) {
        if (extensions == null) {
            throw new NullPointerException("String of extensions is null");
        }
        final String trimmed = extensions.trim();

        String[] result;

        if (trimmed.isEmpty()) {
            result = new String[0];
        } else {
            result = splitForChar(extensions, ',');
            for (int li = 0; li < result.length; li++) {
                result[li] = result[li].trim().toLowerCase();
            }
        }

        return result;
    }

    public static boolean deleteDirectory(final File directory) {
        if (directory == null) {
            throw new NullPointerException("Argument is null");
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Argument is not a directory");
        }

        boolean result = false;
        if (clearDirectory(directory)) {
            result = directory.delete();
        }
        return result;
    }

    public static boolean clearDirectory(final File directory) {
        if (directory.isDirectory()) {
            final File files[] = directory.listFiles();
            for (final File currentFile : files) {
                if (currentFile.isDirectory()) {
                    if (!clearDirectory(currentFile)) {
                        return false;
                    }
                }

                if (!currentFile.delete()) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static void closeSilently(final Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static BufferedReader makeFileReader(final File file, final String charset, final int bufferSize) throws IOException {
        if (file == null) {
            throw new NullPointerException("File is null");
        }

        if (charset == null) {
            throw new NullPointerException("Charset is null");
        }

        if (!Charset.isSupported(charset)) {
            throw new IllegalArgumentException("Unsupported charset [" + charset + ']');
        }

        BufferedReader result = null;

        if (bufferSize <= 0) {
            result = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } else {
            result = new BufferedReader(new InputStreamReader(new FileInputStream(file)), bufferSize);
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
        if (prefix == null) {
            throw new NullPointerException("Prefix is null");
        }

        if (value == null) {
            throw new NullPointerException("Value is null");
        }

        if (prefix.length() > value.length()) {
            throw new IllegalArgumentException("Prefix is taller than the value");
        }

        return value.substring(prefix.length());
    }

    public static void copyFile(final File source, final File dest) throws IOException {
        if (source == null) {
            throw new NullPointerException("Source file is null");
        }

        if (dest == null) {
            throw new NullPointerException("Destination file is null");
        }

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
            closeSilently(fileSrcInput);
            closeSilently(fileOutput);
            closeSilently(fileDst);
            closeSilently(fileSrc);
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
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return result;
    }

    private static void checkFile(final File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File is null");
        }

        if (!file.exists()) {
            throw new FileNotFoundException("File " + getFilePath(file) + " doesn't exist");
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException("File can't be read because it's not a normal file");
        }
    }
    
    public static String[] readWholeTextFileIntoArray(final File file, final String encoding) throws IOException {
        checkFile(file);
        
        final String enc = encoding == null ? "UTF8" : encoding;

        final BufferedReader srcBufferedReader = PreprocessorUtils.makeFileReader(file, enc, (int) file.length());
        final List<String> strContainer = new ArrayList<String>(1024);
        try {
            while (true) {
                final String nextLine = srcBufferedReader.readLine();
                if (nextLine == null) {
                    break;
                }
                strContainer.add(nextLine);
            }
        } finally {
            srcBufferedReader.close();
        }

        return strContainer.toArray(new String[strContainer.size()]);
    }

    public static byte[] readFileAsByteArray(final File file) throws IOException {
        checkFile(file);
        
        int len = (int)file.length();
        
        final ByteBuffer buffer = ByteBuffer.allocate(len);
        final FileChannel inChannel = new FileInputStream(file).getChannel();
        
        try {
            while(len>0){
               final int  read = inChannel.read(buffer);
               if (read<0){
                   throw new IOException("Can't read whole file ["+getFilePath(file)+'\'');
               }
               len -= read;
            }
        }finally{
            closeSilently(inChannel);
        }
        
        return buffer.array();
    }

    public static String[] splitForSetOperator(final String string) {
        final int index = string.indexOf('=');

        String[] result = null;

        if (index < 0) {
            result = new String[]{string};
        } else {
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
                continue;
            } else {
                buffer.append(curChar);
            }
        }

        if (buffer.length() != 0) {
            tokens.add(buffer.toString());
        }

        return tokens.toArray(new String[tokens.size()]);
    }

    public static String normalizeVariableName(final String name) {
        if (name == null){
            return null;
        }
        
        return name.trim().toLowerCase();
    }
    
    public static String getFilePath(final File file){
        String result = "";
        if (file != null){
            try {
                result = file.getCanonicalPath();
            }catch(IOException ex){
                result = file.getAbsolutePath();
            }
        } 
        return result;
    }

    public static void throwPreprocessorException(final String msg,final String processingString, final File srcFile, final int nextStringIndex, final Throwable cause) throws PreprocessorException {
        throw new PreprocessorException(msg, processingString, new FilePositionInfo[]{new FilePositionInfo(srcFile, nextStringIndex)}, cause);
    }
    

}
