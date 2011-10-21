package com.igormaznitsa.jcpreprocessor.references;

import java.io.File;
import java.util.Collections;
import java.util.List;

public final class IncludeReference
{
        private String[] strings;
        private int stringCounter;
        private String fileName;
        private File file;

        public String[] getStrings() {
            return strings;
        }
        
        public File getFile() {
            return file;
        }
        
        public int getStringCounter() {
            return stringCounter;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public IncludeReference(final File currentFile,final String fileName, final String [] stringsArray, final int stringCounter)
        {
            this.file = currentFile;
            this.fileName = fileName;
            this.strings = stringsArray;
            this.stringCounter = stringCounter;
        }
}
