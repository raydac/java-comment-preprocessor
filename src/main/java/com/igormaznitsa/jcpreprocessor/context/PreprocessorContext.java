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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PreprocessorContext {

    public static interface SpecialVariableProcessor {

        String[] getVariableNames();

        Value getVariable(String varName, PreprocessorContext context, PreprocessingState state);

        void setVariable(String varName, Value value, PreprocessorContext context, PreprocessingState state);
    }
    
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
    private Set<String> excludedFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.splitForChar(DEFAULT_EXCLUDED_EXTENSIONS, ',')));
    private boolean clearDestinationDirectoryBefore = true;
    private PreprocessorExtension preprocessorExtension;
    private String characterEncoding = DEFAULT_CHARSET;
    
    private final Map<String, Value> globalVarTable = new HashMap<String, Value>();
    private final Map<String, Value> localVarTable = new HashMap<String, Value>();
    private final Map<String, SpecialVariableProcessor> specialVariableProcessors = new HashMap<String, SpecialVariableProcessor>();

    public PreprocessorContext() {
        normalOutStream = System.out;
        errorOutStream = System.err;
        setSourceDirectory(DEFAULT_SOURCE_DIRECTORY).setDestinationDirectory(DEFAULT_DEST_DIRECTORY);

        registerSpecialVariableProcessor(new JCPSpecialVariables());
    }

    private void registerSpecialVariableProcessor(final SpecialVariableProcessor processor) {
        if (processor == null) {
            throw new NullPointerException("Processor is null");
        }

        for (final String varName : processor.getVariableNames()) {
            if (varName == null) {
                throw new NullPointerException("A Special Var name is null");
            }
            if (specialVariableProcessors.containsKey(varName)) {
                throw new IllegalStateException("There is already defined processor for " + varName);
            }
            specialVariableProcessors.put(varName, processor);
        }
    }

    public void info(final String text) {
        if (text == null || normalOutStream == null) {
            return;
        }
        
        normalOutStream.print("INFO: ");
        normalOutStream.println(text);
    }

    public void error(final String text) {
        if (text == null || errorOutStream == null) {
            return;
        }

        errorOutStream.print("ERROR: ");
        errorOutStream.println(text);
    }

    public void warning(final String text) {
        if (text == null || errorOutStream == null) {
            return;
        }

        normalOutStream.print("WARNING: ");
        normalOutStream.println(text);
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

    public File[] getParsedSourceDirectoryAsFiles() throws IOException {
        final String[] splitted = PreprocessorUtils.splitForChar(sourceDirectory, ';');
        final File[] result = new File[splitted.length];
        int index = 0;
        for (final String dirName : splitted) {
            final File dir = new File(dirName);
            if (!dir.exists() || !dir.isDirectory()) {
                throw new IOException("Can't find source directory [" + dir.getAbsolutePath() + ']');
            }
            result[index++] = dir;
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

    public PreprocessorContext addGlobalVariable(final String valueDescription, final PreprocessingState state) {
        final String[] pair = PreprocessorUtils.splitForChar(valueDescription, '=');
        if (pair.length != 2) {
            throw new IllegalArgumentException("Wrong variable definition format [" + valueDescription + ']');
        }

        if (globalVarTable.containsKey(pair[0])) {
            throw new IllegalStateException("Duplicated global variable \'" + pair[0] + '\'');
        }

        Value calculatedValue = null;
        try {
            calculatedValue = Expression.eval(pair[1], this, state);
            if (calculatedValue == null) {
                throw new RuntimeException("Error value [" + valueDescription + ']');
            }
        } catch (Exception e) {
            throw new RuntimeException("Error value for the global variable \'" + pair[0] + "\' [" + e.getMessage() + "]", e);
        }

        if (isVerbose()) {
            info("The global variable \'" + pair[0] + "\' has been added");
        }

        return this;
    }

    public PreprocessorContext setLocalVariable(final String name, final Value value) {
        if (specialVariableProcessors.containsKey(name) || globalVarTable.containsKey(name)) {
            throw new RuntimeException("Attemption to set a global variable [" + name + ']');
        }
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

    public PreprocessorContext setGlobalVariable(final String name, final Value value, final PreprocessingState state) {
        if (name == null) {
            throw new NullPointerException("Variable name is null");
        }

        if (value == null) {
            throw new NullPointerException("Value is null");
        }

        if (localVarTable.containsKey(name)) {
            throw new RuntimeException("Attemption to set a global variable for name contained among local variables [" + name + ']');
        }

        if (specialVariableProcessors.containsKey(name)) {
            specialVariableProcessors.get(name).setVariable(name, value, this, state);
        } else {

            globalVarTable.put(name, value);

            if (isVerbose()) {
                info("A global variable has been set [" + name + '=' + value.toString() + ']');
            }
        }
        return this;
    }

    public boolean containsGlobalVariable(final String name) {
        if (name == null) {
            return false;
        }

        return specialVariableProcessors.containsKey(name) || globalVarTable.containsKey(name);
    }

    public Value findVariableForName(final String name, final PreprocessingState state) {
        if (name == null) {
            return null;
        }

        final String nameInLowerCase = name.toLowerCase();
        
        final SpecialVariableProcessor processor = specialVariableProcessors.get(nameInLowerCase);
        if (processor != null && state!=null) {
            return processor.getVariable(nameInLowerCase, this, state);
        }

        final Value val = getLocalVariable(nameInLowerCase);
        if (val != null) {
            return val;
        }

        return globalVarTable.get(name);
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
        if (!Charset.isSupported(characterEncoding)) {
            throw new IllegalArgumentException("Unsupported character encoding [" + characterEncoding + ']');
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

        return new File(getDestinationDirectoryAsFile(), file);
    }

    public File getSourceFile(final String file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File is null");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File name is an empty string");
        }

        File result = null;

        if (file.charAt(0) == '.') {
            result = new File(getSourceDirectoryAsFile(), file);
        } else {
            result = new File(file);
        }

        if (!result.isFile() || !result.exists()) {
            throw new FileNotFoundException("File " + result.getAbsolutePath() + " is not found");
        }
        return result;
    }
}
