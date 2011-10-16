package com.igormaznitsa.jcpreprocessor.cfg;

import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.extension.PreprocessorExtension;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Configurator {

    public static final String DEFAULT_SOURCE_DIRECTORY = "." + File.separatorChar;
    public static final String DEFAULT_DEST_DIRECTORY = ".." + File.separatorChar + "preprocessed";
    public static final String DEFAULT_PROCESSING_EXTENSIONS = "java,txt,htm,html";
    public static final String DEFAULT_EXCLUDED_EXTENSIONS = "xml";
    public static final String DEFAULT_CHARSET = "UTF8";
    
    private boolean verbose = false;
    private PrintStream normalOutStream = System.out;
    private PrintStream errorOutStream = System.err;
    private boolean removingComments = false;
    private String sourceDirectory = DEFAULT_SOURCE_DIRECTORY;
    private String destinationDirectory = DEFAULT_DEST_DIRECTORY;
    private File destinationDirectoryFile = new File(destinationDirectory);
    
    private Set<String> processingFileExtensions = new HashSet<String>(Arrays.asList(DEFAULT_PROCESSING_EXTENSIONS.split(",")));
    private Set<String> excludedFileExtensions = new HashSet<String>(Arrays.asList(DEFAULT_EXCLUDED_EXTENSIONS.split(",")));
    private boolean clearDestinationDirectoryBefore = true;
    private Map<String, Value> globalVarTable = new HashMap<String, Value>();
    private Map<String, Value> localVarTable = new HashMap<String, Value>();
    private PreprocessorExtension preprocessorExtension;
    private String characterEncoding = DEFAULT_CHARSET;
    
    public Configurator() {
        normalOutStream = System.out;
        errorOutStream = System.err;
    }

    public void info(final String text) {
        if (text == null || normalOutStream == null) {
            return;
        }

        normalOutStream.println(text);
    }

    public void error(final String text) {
        if (text == null || errorOutStream == null) {
            return;
        }

        errorOutStream.println(text);
    }

    public Configurator setRemovingComments(final boolean removingComments) {
        this.removingComments = removingComments;
        return this;
    }

    public boolean isRemovingComments() {
        return this.removingComments;
    }

    public Configurator setSourceDirectory(final String directory) {
        if (directory == null) {
            throw new NullPointerException("Directory is null");
        }

        this.sourceDirectory = directory;

        return this;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public File [] getParsedSourceDirectoryAsFiles() throws IOException {
        final String [] splitted = sourceDirectory.split(";");
        final File [] result = new File[splitted.length];
        int index = 0;
        for(final String dirName : splitted){
            final File dir = new File(dirName);
            if (!dir.exists() || !dir.isDirectory()){
                throw new IOException("Can't find source directory ["+dir.getAbsolutePath()+']');
            }
            result [index++] = dir;
        }
        
        return result;
    }
    
    public Configurator setDestinationDirectory(final String directory) {
        if (directory == null) {
            throw new NullPointerException("Directory is null");
        }

        this.destinationDirectory = directory;

        destinationDirectoryFile = new File(this.destinationDirectory);
        
        return this;
    }

    public File getDestinationDirectoryAsFile() {
        return destinationDirectoryFile;
    }
    
    public String getDestinationDirectory() {
        return destinationDirectory;
    }

    
    
    public Configurator setProcessingFileExtensions(final String extensions) {
        processingFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.extractExtensions(extensions)));
        return this;
    }

    public String[] getProcessingFileExtensions() {
        return processingFileExtensions.toArray(new String[processingFileExtensions.size()]);
    }

    public final boolean isFileAllowedToBeProcessed(final File file) {
        if (file == null || file.isDirectory()) {
            return false;
        }

        return processingFileExtensions.contains(PreprocessorUtils.getFileExtension(file));
    }

    public final boolean isFileExcludedFromProcess(final File file) {
        if (file == null || file.isDirectory()) {
            return false;
        }

        return excludedFileExtensions.contains(PreprocessorUtils.getFileExtension(file));
    }

    public Configurator setExcludedFileExtensions(final String extensions) {
        excludedFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.extractExtensions(extensions)));
        return this;
    }

    public String[] getExcludedFileExtensions() {
        return excludedFileExtensions.toArray(new String[excludedFileExtensions.size()]);
    }

    public Configurator setClearDestinationDirBefore(final boolean clearDir) {
        this.clearDestinationDirectoryBefore = clearDir;
        return this;
    }

    public boolean doesClearDestinationDirBefore() {
        return this.clearDestinationDirectoryBefore;
    }

    public Configurator setNormalOutStream(final PrintStream stream) {
        normalOutStream = stream;
        return this;
    }

    public Configurator setErrorOutStream(final PrintStream stream) {
        errorOutStream = stream;
        return this;
    }

    public Configurator addGlobalVariable(final String valueDescription) {
        final String[] pair = valueDescription.split("=");
        if (pair.length != 2) {
            throw new IllegalArgumentException("Wrong variable definition format [" + valueDescription + ']');
        }

        if (globalVarTable.containsKey(pair[0])) {
            throw new IllegalStateException("Duplicated global variable \'"+pair[0]+'\'');
        }
        
        Value calculatedValue = null;
        try {
            calculatedValue = Expression.evaluateFormula(null, pair[1],this);
            if (calculatedValue == null) {
                throw new RuntimeException("Error value [" + valueDescription + ']');
            }
        } catch (IOException e) {
            throw new RuntimeException("Error value for the global variable \'" + pair[0] + "\' [" + e.getMessage() + "]",e);
        }
        
        if (isVerbose()) {
            info("The global variable \'"+pair[0]+"\' has been added");
        }
        
        return this;
    }

    public Configurator setLocalVariable(final String name, final Value value) {
        localVarTable.put(name, value);
        return this;
    }
    
    public Value getLocalVariable(final String name) {
        if (name == null) {
            return null;
        }
        return localVarTable.get(name);
    }
    
    public boolean containsLocalVariable(final String name) {
        if (name == null) {
            return false;
        }
        return localVarTable.containsKey(name);
    }
    
    public Configurator clearLocalVariables() {
        localVarTable.clear();
        return this;
    }
    
    public Configurator setGlobalVariable(final String name, final Value value) {
        globalVarTable.put(name, value);

        if (isVerbose()) {
            info("A global variable has been set ["+name+'='+value.toString()+']');
        }
        
        return this;
    }

    public Value getGlobalVariable(final String value) {
        if (value == null) {
            return null;
        }
        return globalVarTable.get(value);
    }

    public boolean containsGlobalVariable(final String name) {
        if (name == null) {
            return false;
        }

        return globalVarTable.containsKey(name);
    }

    public Map<String,Value> getGlobalVariableMap() {
        return Collections.unmodifiableMap(globalVarTable);
    }
    
    public Value removeGlobalVariable(final String name) {
        if (name == null) {
            return null;
        }

        return globalVarTable.remove(name);
    }

    public Value findVariableForName(final String name) {
        if (name == null) {
            return null;
        }
        
        Value val = getLocalVariable(name);
        if (val != null) {
            return val;
        }
        
        return getGlobalVariable(name);
    }
    
    public Configurator setVerbose(final boolean flag) {
        verbose = flag;
        return this;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public Configurator setPreprocessorExtension(final PreprocessorExtension extension) {
        this.preprocessorExtension = extension;
        return this;
    }

    public PreprocessorExtension getPreprocessorExtension() {
        return preprocessorExtension;
    }

    public Configurator setCharacterEncoding(final String characterEncoding) {
        if (!Charset.isSupported(characterEncoding)){
            throw new IllegalArgumentException("Unsupported character encoding ["+characterEncoding+']');
        }
        this.characterEncoding = characterEncoding;
        return this;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public PrintStream getInfoPrintStream() {
        return normalOutStream;
    }

    public PrintStream getErrorPrintStream() {
        return errorOutStream;
    }

}
