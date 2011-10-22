package com.igormaznitsa.jcpreprocessor.references;

import com.igormaznitsa.jcpreprocessor.directives.ParameterContainer;
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
        
        public IncludeReference(final File currentFile,final ParameterContainer container)
        {
            this.file = currentFile;
            this.fileName = container.getCurrentFileCanonicalPath();
            this.strings = container.getStrings();
            this.stringCounter = container.getCurrentStringIndex();
        }
}
