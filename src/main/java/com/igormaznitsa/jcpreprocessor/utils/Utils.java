package com.igormaznitsa.jcpreprocessor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

public class Utils {

    public static final String getFileExtension(final File file) {
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
        String trimmed = extensions.trim();

        String[] result;

        if (trimmed.isEmpty()) {
            result = new String[0];
        } else {
            result = extensions.split(",");
            for (int li = 0; li < result.length; li++) {
                result[li] = result[li].trim();
            }
        }

        return result;
    }

    public static final boolean deleteDirectory(final File directory) {
        if (directory == null)
            throw new NullPointerException("Argument is null");
        
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Argument is not a directory");
        }
        if (clearDirectory(directory)) {
            return directory.delete();
        }
        return false;
    }
    
    public static final boolean clearDirectory(final File directory) {
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

    public static void closeSilently(final Channel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException ex) {
            }
        }
    }

    public static void copyFile(final File source, File dest) throws IOException {
        if (source == null)
            throw new NullPointerException("Source file is null");
        
        if (dest == null)
            throw new NullPointerException("Destination file is null");
        
        if (source.isDirectory())
            throw new IllegalArgumentException("Source file is directory");
        
        
        final FileChannel fileSrc = new FileInputStream(source).getChannel();
        try {

            final FileChannel fileDest = new FileOutputStream(dest).getChannel();
            try {
                long size = fileSrc.size();
                long pos = 0L;
                while(size>0) {
                   final long  written = fileSrc.transferTo(pos, size, fileDest);
                   pos += written;
                   size -= written;
                }
            } finally {
                closeSilently(fileDest);
            }
        } finally {
            closeSilently(fileSrc);
        }
    }
}
