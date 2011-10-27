package com.igormaznitsa.jcpreprocessor.utils;

import com.igormaznitsa.jcpreprocessor.JCPreprocessor;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.extension.PreprocessorExtension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public enum PreprocessorUtils {

    ;
    public static String getFileExtension(final File file) {
        if (file == null) {
            return null;
        }

        final String fileName = file.getName();
        final int lastPointPos = fileName.lastIndexOf('.');
        if (lastPointPos < 0) {
            return "";
        } else {
            return fileName.substring(lastPointPos + 1);
        }
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
        if (clearDirectory(directory)) {
            return directory.delete();
        }
        return false;
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

    public static void closeChannelSilently(final Channel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException ex) {
            }
        }
    }

    public static void closeReaderSilently(final Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException ex) {
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

        if (bufferSize <= 0) {
            return new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } else {
            return new BufferedReader(new InputStreamReader(new FileInputStream(file)), bufferSize);
        }
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
            throw new IOException("Can't make directory [" + dest.getParentFile().getCanonicalPath() + ']');
        }


        final FileChannel fileSrc = new FileInputStream(source).getChannel();
        try {

            final FileChannel fileDest = new FileOutputStream(dest).getChannel();
            try {
                long size = fileSrc.size();
                long pos = 0L;
                while (size > 0) {
                    final long written = fileSrc.transferTo(pos, size, fileDest);
                    pos += written;
                    size -= written;
                }
            } finally {
                closeChannelSilently(fileDest);
            }
        } finally {
            closeChannelSilently(fileSrc);
        }
    }

    public static final String processMacroses(final String processingString,final PreprocessorContext context) {
        int i_indx;

        String result = processingString;
        
        while (true) {
            i_indx = result.indexOf("/*$");

            if (i_indx >= 0) {
                String s_leftpart = result.substring(0, i_indx);
                int i_begin = i_indx;
                i_indx = result.indexOf("$*/", i_indx);
                if (i_indx >= 0) {
                    String s_strVal = result.substring(i_begin + 3, i_indx);
                    String s_rightPart = result.substring(i_indx + 3);

                    Value p_val = Expression.eval(s_strVal,context);
                    if (p_val == null) {
                        throw new RuntimeException("Wrong macros expression ["+s_strVal+']');
                    }

                    result = s_leftpart + p_val.toString() + s_rightPart;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return result;
    }

    public static String generateStringWithPrecendingSpaces(final int spacesCounter, final String tail) {
        if (spacesCounter == 0) {
            return tail;
        }

        final int minimalCapacity = spacesCounter + tail.length();
        
        final StringBuilder result = new StringBuilder(minimalCapacity);
        for (int li = 0; li < spacesCounter; li++) {
            result.append(' ');
        }
        return result.append(tail).toString();
    }

    public static PreprocessorExtension getPreprocessorExtension(final PreprocessorContext cfg) {
        return cfg == null ? null : cfg.getPreprocessorExtension();
    }

    public static boolean isCharAllowedAtHexNumber(final char chr) {
        return (chr >= 'a' && chr <= 'f') || (chr >= 'A' && chr <= 'F') || (chr >= '0' && chr <= '9');
    }

    public static boolean isCharAllowedInVariableOrFunctionName(final char chr) {
        return chr == '_' || Character.isLetterOrDigit(chr);
    }

    public static String[] readWholeTextFileIntoArray(final File file, final String encoding) throws IOException {
        if (file == null) {
            throw new NullPointerException("File is null");
        }

        if (!file.exists()) {
            throw new FileNotFoundException("File "+file.getAbsolutePath()+" doesn't exist");
        }
        
        if (!file.isFile()) {
            throw new IllegalArgumentException("File can't be read because it's not a normal file");
        }

        final String enc = encoding == null ? "UTF8" : encoding;

        final BufferedReader srcBufferedReader = PreprocessorUtils.makeFileReader(file, enc,(int)file.length());
        final List<String> currentFileStringContainer = new ArrayList<String>(1024);
        try {
            while (true) {
                final String nextLine = srcBufferedReader.readLine();
                if (nextLine == null) {
                    break;
                }
                currentFileStringContainer.add(nextLine);
            }
        } finally {
            srcBufferedReader.close();
        }

        return currentFileStringContainer.toArray(new String[currentFileStringContainer.size()]);
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
}
