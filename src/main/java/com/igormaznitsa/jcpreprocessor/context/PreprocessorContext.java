package com.igormaznitsa.jcpreprocessor.context;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.expression.Expression;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.extension.PreprocessorExtension;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PreprocessorContext {

    public static final String DEFAULT_SOURCE_DIRECTORY = "." + File.separatorChar;
    public static final String DEFAULT_DEST_DIRECTORY = ".." + File.separatorChar + "preprocessed";
    public static final String DEFAULT_PROCESSING_EXTENSIONS = "java,txt,htm,html";
    public static final String DEFAULT_EXCLUDED_EXTENSIONS = "xml";
    public static final String DEFAULT_CHARSET = "UTF8";
    
    private boolean verbose = false;
    private PrintStream normalOutStream = System.out;
    private PrintStream errorOutStream = System.err;
    private boolean removingComments = false;
    private String sourceDirectory;
    private String destinationDirectory;
    private File destinationDirectoryFile;
    private File sourceDirectoryFile;
    
    private Set<String> processingFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.splitForChar(DEFAULT_PROCESSING_EXTENSIONS, ',')));
    private Set<String> excludedFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.splitForChar(DEFAULT_EXCLUDED_EXTENSIONS,',')));
    private boolean clearDestinationDirectoryBefore = true;
    private final Map<String, Value> globalVarTable = new HashMap<String, Value>();
    private final Map<String, Value> localVarTable = new HashMap<String, Value>();
    private PreprocessorExtension preprocessorExtension;
    private String characterEncoding = DEFAULT_CHARSET;
    
    public PreprocessorContext() {
        normalOutStream = System.out;
        errorOutStream = System.err;
        setSourceDirectory(DEFAULT_SOURCE_DIRECTORY).setDestinationDirectory(DEFAULT_DEST_DIRECTORY);
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

    public PreprocessorContext setRemovingComments(final boolean removingComments) {
        this.removingComments = removingComments;
        return this;
    }

    public boolean isRemovingComments() {
        return this.removingComments;
    }

    public PreprocessorContext setSourceDirectory(final String directory) {
        if (directory == null) {
            throw new NullPointerException("Directory is null");
        }

        this.sourceDirectory = directory;
        this.sourceDirectoryFile = new File(sourceDirectory);

        return this;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }
    
    public File getSourceDirectoryAsFile() {
        return sourceDirectoryFile;
    }

    public File [] getParsedSourceDirectoryAsFiles() throws IOException {
        final String [] splitted = PreprocessorUtils.splitForChar(sourceDirectory, ';');
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
    
    public PreprocessorContext setDestinationDirectory(final String directory) {
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
    
    public PreprocessorContext setProcessingFileExtensions(final String extensions) {
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

    public PreprocessorContext setExcludedFileExtensions(final String extensions) {
        excludedFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.extractExtensions(extensions)));
        return this;
    }

    public String[] getExcludedFileExtensions() {
        return excludedFileExtensions.toArray(new String[excludedFileExtensions.size()]);
    }

    public PreprocessorContext setClearDestinationDirBefore(final boolean clearDir) {
        this.clearDestinationDirectoryBefore = clearDir;
        return this;
    }

    public boolean doesClearDestinationDirBefore() {
        return this.clearDestinationDirectoryBefore;
    }

    public PreprocessorContext setNormalOutStream(final PrintStream stream) {
        normalOutStream = stream;
        return this;
    }

    public PreprocessorContext setErrorOutStream(final PrintStream stream) {
        errorOutStream = stream;
        return this;
    }

    public PreprocessorContext addGlobalVariable(final String valueDescription) {
        final String[] pair = PreprocessorUtils.splitForChar(valueDescription, '=');
        if (pair.length != 2) {
            throw new IllegalArgumentException("Wrong variable definition format [" + valueDescription + ']');
        }

        if (globalVarTable.containsKey(pair[0])) {
            throw new IllegalStateException("Duplicated global variable \'"+pair[0]+'\'');
        }
        
        Value calculatedValue = null;
        try {
            calculatedValue = Expression.eval(pair[1],this);
            if (calculatedValue == null) {
                throw new RuntimeException("Error value [" + valueDescription + ']');
            }
        } catch (Exception e) {
            throw new RuntimeException("Error value for the global variable \'" + pair[0] + "\' [" + e.getMessage() + "]",e);
        }
        
        if (isVerbose()) {
            info("The global variable \'"+pair[0]+"\' has been added");
        }
        
        return this;
    }

    public PreprocessorContext setLocalVariable(final String name, final Value value) {
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
    
    public PreprocessorContext clearLocalVariables() {
        localVarTable.clear();
        return this;
    }
    
    public PreprocessorContext setGlobalVariable(final String name, final Value value) {
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
    
    public PreprocessorContext setVerbose(final boolean flag) {
        verbose = flag;
        return this;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public PreprocessorContext setPreprocessorExtension(final PreprocessorExtension extension) {
        this.preprocessorExtension = extension;
        return this;
    }

    public PreprocessorExtension getPreprocessorExtension() {
        return preprocessorExtension;
    }

    public PreprocessorContext setCharacterEncoding(final String characterEncoding) {
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

    public File makeDestinationFile(final String file) {
        if (file == null) {
            throw new NullPointerException("File is null");
        }
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File name is an empty string");
        }
        
        return new File(getDestinationDirectoryAsFile(),file);
    }
    
    public File getSourceFile(final String file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File is null");
        }
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File name is an empty string");
        }
        
        File result = null;
        
        if (file.charAt(0) == '.')
        {
            result = new File(getSourceDirectoryAsFile(),file);
        } else {
            result = new File(file);
        }
        
        if (!result.isFile() || !result.exists()) {
            throw new FileNotFoundException("File "+result.getAbsolutePath()+" is not found");
        }
        return result;
    }
}
