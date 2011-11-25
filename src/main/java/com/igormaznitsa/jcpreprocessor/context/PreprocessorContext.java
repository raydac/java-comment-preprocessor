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
package com.igormaznitsa.jcpreprocessor.context;

import com.igormaznitsa.jcpreprocessor.containers.FileInfoContainer;
import com.igormaznitsa.jcpreprocessor.containers.TextFileDataContainer;
import com.igormaznitsa.jcpreprocessor.expression.Value;
import com.igormaznitsa.jcpreprocessor.extension.PreprocessorExtension;
import com.igormaznitsa.jcpreprocessor.logger.PreprocessorLogger;
import com.igormaznitsa.jcpreprocessor.logger.SystemOutLogger;
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The preprocessor context class is a main class which contains all options of the preprocessor and allows to work with variables in expressions
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class PreprocessorContext {
    
    public static final String DEFAULT_SOURCE_DIRECTORY = "." + File.separatorChar;
    public static final String DEFAULT_DEST_DIRECTORY = ".." + File.separatorChar + "preprocessed";
    public static final String DEFAULT_PROCESSING_EXTENSIONS = "java,txt,htm,html";
    public static final String DEFAULT_EXCLUDED_EXTENSIONS = "xml";
    public static final String DEFAULT_CHARSET = "UTF8";
    
    private boolean verbose = false;
    private boolean removeComments = false;
    private boolean clearDestinationDirectoryBefore = true;
    private boolean fileOutputDisabled = false;

    private String sourceDirectory;
    private String destinationDirectory;
    private File destinationDirectoryFile;
    private File sourceDirectoryFile;
    private Set<String> processingFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.splitForChar(DEFAULT_PROCESSING_EXTENSIONS, ',')));
    private Set<String> excludedFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.splitForChar(DEFAULT_EXCLUDED_EXTENSIONS, ',')));
    private PreprocessorExtension preprocessorExtension;
    private String inCharacterEncoding = DEFAULT_CHARSET;
    private String outCharacterEncoding = DEFAULT_CHARSET;
    
    private final Map<String, Value> globalVarTable = new HashMap<String, Value>();
    private final Map<String, Value> localVarTable = new HashMap<String, Value>();
    private final Map<String, SpecialVariableProcessor> specialVariableProcessors = new HashMap<String, SpecialVariableProcessor>();
    private final Map<String,Object> sharedResources = new HashMap<String,Object>();
    
    private PreprocessorLogger preprocessorLogger = new SystemOutLogger();
    
    private final List<File> globalVarDefiningFiles = new ArrayList<File>(); 
    
    private PreprocessingState currentState;
    
    public PreprocessorContext() {
        setSourceDirectory(DEFAULT_SOURCE_DIRECTORY).setDestinationDirectory(DEFAULT_DEST_DIRECTORY);
        registerSpecialVariableProcessor(new JCPSpecialVariableProcessor());
        registerSpecialVariableProcessor(new EnvironmentVariableProcessor());
    }

    public void setPreprocessorLogger(final PreprocessorLogger logger){
        preprocessorLogger = logger;
    }
    
    public void registerSpecialVariableProcessor(final SpecialVariableProcessor processor) {
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

    public void logInfo(final String text) {
        
        if (text == null || preprocessorLogger == null) {
            return;
        }
        
        preprocessorLogger.info(text);
    }

    public void logError(final String text) {
        if (text == null || preprocessorLogger == null) {
            return;
        }
        preprocessorLogger.error(text);
    }

    public void logWarning(final String text) {
        if (text == null || preprocessorLogger == null) {
            return;
        }
        preprocessorLogger.warning(text);
    }
    
    public PreprocessorContext setRemoveComments(final boolean removingComments) {
        this.removeComments = removingComments;
        return this;
    }

    public boolean isRemoveComments() {
        return this.removeComments;
    }

    public void setFileOutputDisabled(final boolean flag){
        fileOutputDisabled = flag;
    }
    
    public boolean isFileOutputDisabled(){
        return fileOutputDisabled;
    }
    
    public PreprocessorContext setSourceDirectory(final String directory) {
        if (directory == null) {
            throw new NullPointerException("Directory is null");
        }

        this.sourceDirectory = directory;
        this.sourceDirectoryFile = new File(sourceDirectory);

        return this;
    }

    public void setSharedResource(final String name, final Object obj){
        if (name == null) {
            throw new NullPointerException("Name is null");
        }
        
        if (obj == null) {
            throw new NullPointerException("Object is null");
        }
        
        sharedResources.put(name, obj);
    }
    
    public Object getSharedResource(final String name){
        if (name == null) {
            throw new NullPointerException("Name is null");
        }
        return sharedResources.get(name);
    }
    
    public Object removeSharedResource(final String name){
        if (name == null) {
            throw new NullPointerException("Name is null");
        }
        return sharedResources.remove(name);
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
                throw new IOException("Can't find source directory [" + PreprocessorUtils.getFilePath(dir) + ']');
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
        if (file == null || !file.exists() || file.isDirectory()) {
            return false;
        }

        return processingFileExtensions.contains(PreprocessorUtils.getFileExtension(file));
    }

    public final boolean isFileExcludedFromProcess(final File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
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

    public PreprocessorContext setLocalVariable(final String name, final Value value) {
        if (name == null) {
            throw new NullPointerException("Variable name is null");
        }

        if (value == null) {
            throw new NullPointerException("Value is null");
        }
        
        final String normalized = PreprocessorUtils.normalizeVariableName(name);
        
        if (normalized.isEmpty()){
            throw new IllegalArgumentException("Empty variable name");
        }
        
        if (specialVariableProcessors.containsKey(normalized) || globalVarTable.containsKey(normalized)) {
            throw new IllegalArgumentException("Attemption to set a global variable or a special variable as a local one [" + normalized + ']');
        }
        localVarTable.put(normalized, value);
        return this;
    }

    public Value getLocalVariable(final String name) {
        if (name == null) {
            return null;
        }
        
        final String normalized = PreprocessorUtils.normalizeVariableName(name);
        
        if (normalized.isEmpty()){
            return null;
        }
        
        return localVarTable.get(normalized);
    }

    public boolean containsLocalVariable(final String name) {
        if (name == null) {
            return false;
        }
        
        final String normalized = PreprocessorUtils.normalizeVariableName(name);
        
        if (normalized.isEmpty()){
            return false;
        }
        
        return localVarTable.containsKey(normalized);
    }

    public PreprocessorContext clearLocalVariables() {
        localVarTable.clear();
        return this;
    }

    public PreprocessorContext setGlobalVariable(final String name, final Value value) {
        if (name == null) {
            throw new NullPointerException("Variable name is null");
        }

        final String normalizedName = PreprocessorUtils.normalizeVariableName(name);

        if (normalizedName.isEmpty()){
            throw new IllegalArgumentException("Name is empty");
        }
        
        if (value == null) {
            throw new NullPointerException("Value is null");
        }

        if (specialVariableProcessors.containsKey(normalizedName)) {
            specialVariableProcessors.get(normalizedName).setVariable(normalizedName, value, this);
        } else {

            globalVarTable.put(normalizedName, value);

            if (isVerbose()) {
                logInfo("A global variable has been set [" + normalizedName + '=' + value.toString() + ']');
            }
        }
        return this;
    }

    public boolean containsGlobalVariable(final String name) {
        if (name == null) {
            return false;
        }

        final String normalized = PreprocessorUtils.normalizeVariableName(name);
        if (normalized.isEmpty()){
            return false;
        }
        
        return specialVariableProcessors.containsKey(normalized) || globalVarTable.containsKey(normalized);
    }

    public Value findVariableForName(final String name) {
        if (name == null) {
            return null;
        }

        final String normalized = PreprocessorUtils.normalizeVariableName(name);
        
        if (normalized.isEmpty()){
            return null;
        }
        
        final SpecialVariableProcessor processor = specialVariableProcessors.get(normalized);
        
        if (processor != null && currentState!=null) {
            return processor.getVariable(normalized, this);
        }

        final Value val = getLocalVariable(normalized);
        if (val != null) {
            return val;
        }

        return globalVarTable.get(normalized);
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

    public PreprocessorContext setInCharacterEncoding(final String characterEncoding) {
        if (!Charset.isSupported(characterEncoding)) {
            throw new IllegalArgumentException("Unsupported character encoding [" + characterEncoding + ']');
        }
        this.inCharacterEncoding = characterEncoding;
        return this;
    }

    public PreprocessorContext setOutCharacterEncoding(final String characterEncoding) {
        if (!Charset.isSupported(characterEncoding)) {
            throw new IllegalArgumentException("Unsupported character encoding [" + characterEncoding + ']');
        }
        this.outCharacterEncoding = characterEncoding;
        return this;
    }

    public String getInCharacterEncoding() {
        return inCharacterEncoding;
    }

    public String getOutCharacterEncoding() {
        return outCharacterEncoding;
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

        String parentDir = null;
        if (currentState!=null && currentState.peekFile()!=null){
            parentDir = currentState.peekFile().getFile().getParent();
        }
        
        if (file.charAt(0) == '.' && parentDir != null) {
            result = new File(parentDir, file);
        } else {
            result = new File(getSourceDirectoryAsFile(), file);
        }

        if (!result.isFile() || !result.exists()) {
            throw new FileNotFoundException("File " + PreprocessorUtils.getFilePath(result) + " is not found");
        }
        return result;
    }

    public void addGlobalVarDefiningFile(final File file) {
        if (file == null) {
            throw new NullPointerException("File is null");
        }
        globalVarDefiningFiles.add(file);
    }
    
    public File[] getGLobalVarDefiningFiles(){
        return globalVarDefiningFiles.toArray(new File[globalVarDefiningFiles.size()]);
    }
    
    public PreprocessingState produceNewPreprocessingState(final FileInfoContainer rootFile) throws IOException {
        this.currentState = new PreprocessingState(rootFile, getInCharacterEncoding(), getOutCharacterEncoding());
        return this.currentState;
    }

    public PreprocessingState produceNewPreprocessingState(final FileInfoContainer rootFile, final TextFileDataContainer rootContainer) throws IOException {
        this.currentState = new PreprocessingState(rootFile, rootContainer, getInCharacterEncoding(), getOutCharacterEncoding());
        return this.currentState;
    }
    
    public PreprocessingState getPreprocessingState() {
        return this.currentState;
    }
}
