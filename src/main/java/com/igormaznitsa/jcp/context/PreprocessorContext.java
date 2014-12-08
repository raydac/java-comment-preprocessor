/*
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
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
package com.igormaznitsa.jcp.context;

import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.containers.TextFileDataContainer;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.extension.PreprocessorExtension;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import com.igormaznitsa.jcp.logger.SystemOutLogger;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import org.apache.commons.io.FilenameUtils;

/**
 * The preprocessor context class is a main class which contains all options of
 * the preprocessor and allows to work with variables in expressions
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
  private boolean keepNonExecutingLines = false;
  private boolean careForLastNextLine = false;
  
  private String sourceDirectories;
  private String destinationDirectory;
  private File destinationDirectoryFile;
  private File[] sourceDirectoryFiles;
  private Set<String> processingFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.splitForChar(DEFAULT_PROCESSING_EXTENSIONS, ',')));
  private Set<String> excludedFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.splitForChar(DEFAULT_EXCLUDED_EXTENSIONS, ',')));
  private PreprocessorExtension preprocessorExtension;
  private String inCharacterEncoding = DEFAULT_CHARSET;
  private String outCharacterEncoding = DEFAULT_CHARSET;

  private final Map<String, Value> globalVarTable = new HashMap<String, Value>();
  private final Map<String, Value> localVarTable = new HashMap<String, Value>();
  private final Map<String, SpecialVariableProcessor> mapVariableNameToSpecialVarProcessor = new HashMap<String, SpecialVariableProcessor>();
  private final Map<String, Object> sharedResources = new HashMap<String, Object>();

  private PreprocessorLogger preprocessorLogger = new SystemOutLogger();

  private final List<File> configFiles = new ArrayList<File>();

  private transient PreprocessingState currentState;
  private final boolean cloned;

  private final TextFileDataContainer currentInCloneSource;
  
  /**
   * The constructor
   */
  public PreprocessorContext() {
    setSourceDirectories(DEFAULT_SOURCE_DIRECTORY).setDestinationDirectory(DEFAULT_DEST_DIRECTORY);
    registerSpecialVariableProcessor(new JCPSpecialVariableProcessor());
    registerSpecialVariableProcessor(new EnvironmentVariableProcessor());
    this.cloned = false;
    this.currentInCloneSource = null;
  }

  /**
   * Set the flag to care to be precise in processing the last file next line char
   * @param flag true to turn on the mode, false to turn off
   */
  public void setCareForLastNextLine(final boolean flag){
    this.careForLastNextLine = flag;
  }
  
  public boolean isCareForLastNextLine(){
    return this.careForLastNextLine;
  }
  
  /**
   * Check that the preprocessor context is a clone of another context.
   *
   * @return true if the context is a clone, false otherwise
   */
  public boolean isCloned() {
    return this.cloned;
  }

  /**
   * Make clone of a preprocessor context but without cloning state.
   *
   * @param context the context to be cloned, must not be null.
   */
  public PreprocessorContext(final PreprocessorContext context) {
    PreprocessorUtils.assertNotNull("Source context must not be null", context);

    this.verbose = context.verbose;
    this.removeComments = context.removeComments;
    this.clearDestinationDirectoryBefore = context.clearDestinationDirectoryBefore;
    this.fileOutputDisabled = context.fileOutputDisabled;
    this.keepNonExecutingLines = context.keepNonExecutingLines;
    this.sourceDirectories = context.sourceDirectories;
    this.destinationDirectory = context.destinationDirectory;
    this.destinationDirectoryFile = context.destinationDirectoryFile;
    this.sourceDirectoryFiles = context.sourceDirectoryFiles.clone();

    this.careForLastNextLine = context.careForLastNextLine;
    
    this.processingFileExtensions.clear();
    this.processingFileExtensions.addAll(context.processingFileExtensions);

    this.excludedFileExtensions.clear();
    this.excludedFileExtensions.addAll(context.excludedFileExtensions);

    this.preprocessorExtension = context.preprocessorExtension;
    this.inCharacterEncoding = context.inCharacterEncoding;
    this.outCharacterEncoding = context.outCharacterEncoding;

    this.globalVarTable.putAll(context.globalVarTable);
    this.localVarTable.putAll(context.localVarTable);
    this.mapVariableNameToSpecialVarProcessor.putAll(context.mapVariableNameToSpecialVarProcessor);
    this.sharedResources.putAll(context.sharedResources);

    this.configFiles.addAll(context.configFiles);

    this.currentState = context.currentState;
    this.cloned = true;
    this.currentInCloneSource = context.getPreprocessingState() == null ? null : context.getPreprocessingState().peekFile();
  }

  /**
   * Set the logger to print information and error messages
   *
   * @param logger a logger to be used for output, it can be null
   */
  public void setPreprocessorLogger(final PreprocessorLogger logger) {
    preprocessorLogger = logger;
  }

  /**
   * It allows to register a special variable processor which can process some
   * special global variables
   *
   * @param processor a variable processor to be registered, it must not be null
   * @see SpecialVariableProcessor
   */
  public void registerSpecialVariableProcessor(final SpecialVariableProcessor processor) {
    PreprocessorUtils.assertNotNull("Processor is null", processor);

    for (final String varName : processor.getVariableNames()) {
      PreprocessorUtils.assertNotNull("A Special Var name is null", varName);
      if (mapVariableNameToSpecialVarProcessor.containsKey(varName)) {
        throw new IllegalStateException("There is already defined processor for " + varName);
      }
      mapVariableNameToSpecialVarProcessor.put(varName, processor);
    }
  }

  /**
   * Print an information into the current log
   *
   * @param text a String to be printed into the information log, it can be null
   */
  public void logInfo(final String text) {

    if (text == null || preprocessorLogger == null) {
      return;
    }

    preprocessorLogger.info(text);
  }

  /**
   * Print an information about an error into the current log
   *
   * @param text a String to be printed into the error log, it can be null
   */
  public void logError(final String text) {
    if (text == null || preprocessorLogger == null) {
      return;
    }
    preprocessorLogger.error(text);
  }

  /**
   * Print an information about a warning situation into the current log
   *
   * @param text a String to be printed into the warning log, it can be null
   */
  public void logWarning(final String text) {
    if (text == null || preprocessorLogger == null) {
      return;
    }
    preprocessorLogger.warning(text);
  }

  /**
   * Set the remove comments flag
   *
   * @param removingComments the flag to set, true if comments must be removed
   * from the result files, otherwise else
   * @return the preprocessor context instance
   */
  public PreprocessorContext setRemoveComments(final boolean removingComments) {
    this.removeComments = removingComments;
    return this;
  }

  /**
   * It returns the flag shows that all comments must be removed from the result
   *
   * @return true if comments must be returned, otherwise false
   */
  public boolean isRemoveComments() {
    return this.removeComments;
  }

  /**
   * It allows to disable all writing operations of the preprocessor
   *
   * @param flag true if preprocessor must not make any writing operations,
   * otherwise false
   */
  public void setFileOutputDisabled(final boolean flag) {
    fileOutputDisabled = flag;
  }

  /**
   * Check that writing operations is disabled
   *
   * @return true if writing operations disabled, otherwise false
   */
  public boolean isFileOutputDisabled() {
    return fileOutputDisabled;
  }

  /**
   * Set source directories
   *
   * @param directories semi separated list of source directories, must not be
   * null
   * @return this preprocessor context instance
   */
  public PreprocessorContext setSourceDirectories(final String directories) {
    PreprocessorUtils.assertNotNull("Source directory is null", directories);

    this.sourceDirectories = directories;
    this.sourceDirectoryFiles = getParsedSourceDirectoryAsFiles();

    return this;
  }

  /**
   * Set a shared source, it is an object saved into the inside map for a name
   *
   * @param name the name for the saved project, must not be null
   * @param obj the object to be saved in, must not be null
   */
  public void setSharedResource(final String name, final Object obj) {
    PreprocessorUtils.assertNotNull("Name is null", name);
    PreprocessorUtils.assertNotNull("Object is null", obj);

    sharedResources.put(name, obj);
  }

  /**
   * Get a shared source from inside map
   *
   * @param name the name of the needed object, it must not be null
   * @return a cached object or null if it is not found
   */
  public Object getSharedResource(final String name) {
    PreprocessorUtils.assertNotNull("Name is null", name);
    return sharedResources.get(name);
  }

  /**
   * Remove a shared object from the inside map for its name
   *
   * @param name the object name, it must not be null
   * @return removing object or null if it is not found
   */
  public Object removeSharedResource(final String name) {
    PreprocessorUtils.assertNotNull("Name is null", name);
    return sharedResources.remove(name);
  }

  /**
   * Get the source directories as semi separated string
   *
   * @return the current source directories semi separated list
   */
  public String getSourceDirectories() {
    return sourceDirectories;
  }

  /**
   * Get the current source directories as a file array
   *
   * @return the current source directories as a file array
   */
  public File[] getSourceDirectoryAsFiles() {
    return sourceDirectoryFiles;
  }

  /**
   * Inside auxiliary method to parse the source directories list into file
   * array
   *
   * @return parsed file list, each file must exist and be a directory
   */
  private File[] getParsedSourceDirectoryAsFiles() {
    final String[] splitted = PreprocessorUtils.splitForChar(sourceDirectories, ';');
    final File[] result = new File[splitted.length];
    int index = 0;
    for (final String dirName : splitted) {
      final File dir = new File(dirName);
      if (!dir.isDirectory()) {
        throw new IllegalStateException("Can't find a source directory [" + PreprocessorUtils.getFilePath(dir) + ']');
      }
      result[index++] = dir;
    }

    return result;
  }

  /**
   * Set a destination directory for result files
   *
   * @param directory a path to the directory as String, it must not be null
   * @return this preprocessor context instance
   */
  public PreprocessorContext setDestinationDirectory(final String directory) {
    PreprocessorUtils.assertNotNull("Directory is null", directory);
    this.destinationDirectory = directory;
    destinationDirectoryFile = new File(this.destinationDirectory);

    return this;
  }

  /**
   * Get the current destination directory as a File object
   *
   * @return the current destination directory as an object
   */
  public File getDestinationDirectoryAsFile() {
    return destinationDirectoryFile;
  }

  /**
   * Get the string representation of the destination directory
   *
   * @return the current destination directory as a String
   */
  public String getDestinationDirectory() {
    return destinationDirectory;
  }

  /**
   * Set file extensions of files to be preprocessed, it is a comma separated
   * list
   *
   * @param extensions comma separated extensions list of file extensions to be
   * preprocessed, must not be null
   * @return this preprocessor context
   */
  public PreprocessorContext setProcessingFileExtensions(final String extensions) {
    PreprocessorUtils.assertNotNull("Argument is null", extensions);
    processingFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.splitExtensionCommaList(extensions)));
    return this;
  }

  /**
   * Get file extensions of files to be preprocessed as a string array
   *
   * @return a string array of file extensions to be preprocessed
   */
  public String[] getProcessingFileExtensions() {
    return processingFileExtensions.toArray(new String[processingFileExtensions.size()]);
  }

  /**
   * Check that a file is allowed to be preprocessed fo its extension
   *
   * @param file a file to be checked
   * @return true if the file is allowed, false otherwise
   */
  public final boolean isFileAllowedToBeProcessed(final File file) {
    if (file == null || !file.exists() || file.isDirectory() || file.length() == 0) {
      return false;
    }

    return processingFileExtensions.contains(PreprocessorUtils.getFileExtension(file));
  }

  /**
   * Check that a file is excluded from preprocessing and coping actions
   *
   * @param file a file to be checked
   * @return true if th file must be excluded, otherwise false
   */
  public final boolean isFileExcludedFromProcess(final File file) {
    final boolean result;
    if (file != null && file.isFile()) {
      result = excludedFileExtensions.contains(PreprocessorUtils.getFileExtension(file));
    }
    else {
      result = false;
    }
    return result;
  }

  /**
   * Set comma separated list of file extensions to be excluded from
   * preprocessing
   *
   * @param extensions a comma separated file extension list, it must not be
   * null
   * @return this preprocessor context
   */
  public PreprocessorContext setExcludedFileExtensions(final String extensions) {
    PreprocessorUtils.assertNotNull("Argument is null", extensions);
    excludedFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.splitExtensionCommaList(extensions)));
    return this;
  }

  /**
   * Get excluded file extension list as a string array
   *
   * @return a string array contains file extensions to be excluded from
   * preprocessing act
   */
  public String[] getExcludedFileExtensions() {
    return excludedFileExtensions.toArray(new String[excludedFileExtensions.size()]);
  }

  /**
   * Set the flag to clear the destination directory before preprocessing
   *
   * @param flag true if the directory must be cleaned, otherwise false
   * @return this preprocessor context
   */
  public PreprocessorContext setClearDestinationDirBefore(final boolean flag) {
    this.clearDestinationDirectoryBefore = flag;
    return this;
  }

  /**
   * Get the flag to clear the destination directory before preprocessing
   *
   * @return true if the directory must be cleaned, otherwise false
   */
  public boolean doesClearDestinationDirBefore() {
    return this.clearDestinationDirectoryBefore;
  }

  /**
   * Set a local variable value
   *
   * @param name the variable name, must not be null, remember that the name
   * will be normalized and will be entirely in lower case
   * @param value the value for the variable, it must not be null
   * @return this preprocessor context
   * @see Value
   */
  public PreprocessorContext setLocalVariable(final String name, final Value value) {
    PreprocessorUtils.assertNotNull("Variable name is null", name);
    PreprocessorUtils.assertNotNull("Value is null", value);

    final String normalized = PreprocessorUtils.normalizeVariableName(name);

    if (normalized.isEmpty()) {
      throw makeException("Not defined variable name", null);
    }

    if (mapVariableNameToSpecialVarProcessor.containsKey(normalized) || globalVarTable.containsKey(normalized)) {
      throw makeException("Attempting to set either a global variable or a special variable as a local one [" + normalized + ']', null);
    }

    localVarTable.put(normalized, value);
    return this;
  }

  /**
   * Remove a local variable value from the context.
   *
   * @param name the variable name, must not be null, remember that the name
   * will be normalized and will be entirely in lower case
   * @return this preprocessor context
   * @see Value
   */
  public PreprocessorContext removeLocalVariable(final String name) {
    PreprocessorUtils.assertNotNull("Variable name is null", name);
    final String normalized = PreprocessorUtils.normalizeVariableName(name);

    if (normalized.isEmpty()) {
      throw makeException("Empty variable name", null);
    }

    if (mapVariableNameToSpecialVarProcessor.containsKey(normalized) || globalVarTable.containsKey(normalized)) {
      throw makeException("Attempting to remove either a global variable or a special variable as a local one [" + normalized + ']', null);
    }
    
    if (isVerbose()){
      logForVerbose("Removing local variable '"+normalized+"\'");
    }
    localVarTable.remove(normalized);
    return this;
  }

  /**
   * Remove a global variable value from the context.
   *
   * @param name the variable name, must not be null, remember that the name
   * will be normalized and will be entirely in lower case
   * @return this preprocessor context
   * @see Value
   */
  public PreprocessorContext removeGlobalVariable(final String name) {
    PreprocessorUtils.assertNotNull("Variable name is null", name);

    final String normalized = PreprocessorUtils.normalizeVariableName(name);

    if (normalized.isEmpty()) {
      throw makeException("Empty variable name", null);
    }
                          
    if (mapVariableNameToSpecialVarProcessor.containsKey(normalized)) {
      throw makeException("Attempting to remove a special variable as a global one [" + normalized + ']', null);
    }
    
    if (isVerbose()) {
      logForVerbose("Removing global variable '" + normalized + "\'");
    }

    globalVarTable.remove(normalized);
    return this;
  }

  /**
   * Get a local variable value
   *
   * @param name the name for the variable, it can be null. The name will be
   * normalized to allowed one.
   * @return null either if the name is null or the variable is not found,
   * otherwise its value
   */
  public Value getLocalVariable(final String name) {
    if (name == null) {
      return null;
    }

    final String normalized = PreprocessorUtils.normalizeVariableName(name);

    if (normalized.isEmpty()) {
      return null;
    }

    return localVarTable.get(normalized);
  }

  /**
   * Check that a local variable for a name is presented
   *
   * @param name the checking name, it will be normalized to the support format
   * and can be null
   * @return false either if the name is null or there is not any local variable
   * for the name, otherwise true
   */
  public boolean containsLocalVariable(final String name) {
    if (name == null) {
      return false;
    }

    final String normalized = PreprocessorUtils.normalizeVariableName(name);

    if (normalized.isEmpty()) {
      return false;
    }

    return localVarTable.containsKey(normalized);
  }

  /**
   * Remove all local variables from the inside storage
   *
   * @return this preprocessor context
   */
  public PreprocessorContext clearLocalVariables() {
    localVarTable.clear();
    return this;
  }

  /**
   * Set a global variable value
   *
   * @param name the variable name, it must not be null and will be normalized
   * to the supported format
   * @param value the variable value, it must not be null
   * @return this preprocessor context
   */
  public PreprocessorContext setGlobalVariable(final String name, final Value value) {
    PreprocessorUtils.assertNotNull("Variable name is null", name);

    final String normalizedName = PreprocessorUtils.normalizeVariableName(name);

    if (normalizedName.isEmpty()) {
      throw makeException("Name is empty", null);
    }

    PreprocessorUtils.assertNotNull("Value is null", value);

    if (mapVariableNameToSpecialVarProcessor.containsKey(normalizedName)) {
      mapVariableNameToSpecialVarProcessor.get(normalizedName).setVariable(normalizedName, value, this);
    }
    else {
      if (isVerbose()) {
        final String valueAsStr = value.toString();
        if (globalVarTable.containsKey(normalizedName)) {
          logForVerbose("Replacing global variable [" + normalizedName + '=' + valueAsStr + ']');
        }
        else {
          logForVerbose("Defining new global variable [" + normalizedName + '=' + valueAsStr + ']');
        }
      }
      globalVarTable.put(normalizedName, value);
    }
    return this;
  }

  /**
   * Check that there is a named global variable in the inside storage
   *
   * @param name the checking name, it will be normalized to the supported
   * format, it can be null
   * @return true if such variable is presented for its name in the inside
   * storage, otherwise false (also it is false if the name is null)
   */
  public boolean containsGlobalVariable(final String name) {
    if (name == null) {
      return false;
    }

    final String normalized = PreprocessorUtils.normalizeVariableName(name);
    if (normalized.isEmpty()) {
      return false;
    }

    return mapVariableNameToSpecialVarProcessor.containsKey(normalized) || globalVarTable.containsKey(normalized);
  }

  /**
   * Find value among local and global variables for a name. It finds in the
   * order: special processors, local variables, global variables
   *
   * @param name the name for the needed variable, it will be normalized to the
   * supported format
   * @return false if either the variable is not found or the name is null,
   * otherwise the variable value
   */
  public Value findVariableForName(final String name) {
    if (name == null) {
      return null;
    }

    final String normalized = PreprocessorUtils.normalizeVariableName(name);

    if (normalized.isEmpty()) {
      return null;
    }

    final SpecialVariableProcessor processor = mapVariableNameToSpecialVarProcessor.get(normalized);

    if (processor != null && currentState != null) {
      return processor.getVariable(normalized, this);
    }

    final Value val = getLocalVariable(normalized);
    if (val != null) {
      return val;
    }

    return globalVarTable.get(normalized);
  }

  /**
   * Check that there is a global variable with such name.
   *
   * @param variableName a name to be checked, can be null
   * @return false if there is not such variable or it is null, true if such
   * global or special variable exists
   */
  public boolean isGlobalVariable(final String variableName) {
    boolean result = false;
    if (variableName != null) {
      final String normalized = PreprocessorUtils.normalizeVariableName(variableName);
      result = this.globalVarTable.containsKey(normalized) || mapVariableNameToSpecialVarProcessor.containsKey(normalized);
    }
    return result;
  }

  /**
   * Check that there is a local variable with such name.
   *
   * @param variableName a name to be checked, can be null
   * @return false if there is not such variable or it is null, true if such
   * local variable exists
   */
  public boolean isLocalVariable(final String variableName) {
    boolean result = false;
    if (variableName != null) {
      final String normalized = PreprocessorUtils.normalizeVariableName(variableName);
      result = this.localVarTable.containsKey(normalized);
    }
    return result;
  }

  /**
   * Set the verbose flag
   *
   * @param flag true if the preprocessor must be verbose, otherwise false
   * @return this preprocessor context
   */
  public PreprocessorContext setVerbose(final boolean flag) {
    verbose = flag;
    return this;
  }

  /**
   * Check the verbose flag
   *
   * @return true if the preprocessor must be verbose, otherwise false
   */
  public boolean isVerbose() {
    return verbose;
  }

  /**
   * Set the flag to keep lines as commented ones
   *
   * @param flag true if the preprocessor must keep non-executing lines,
   * otherwise false
   * @return this preprocessor context
   */
  public PreprocessorContext setKeepLines(final boolean flag) {
    keepNonExecutingLines = flag;
    return this;
  }

  /**
   * Check that the preprocessor must keep lines as commented ones
   *
   * @return true if the preprocessor must keep lines, false otherwise
   */
  public boolean isKeepLines() {
    return keepNonExecutingLines;
  }

  /**
   * Set a preprocessor extension, it is a module implements the
   * PreprocessorExtension interface which can process and get some calls from a
   * preprocessor during its work
   *
   * @param extension an object implements the PreprocessorExtension interface,
   * it can be null
   * @return this preprocessor context
   * @see PreprocessorExtension
   */
  public PreprocessorContext setPreprocessorExtension(final PreprocessorExtension extension) {
    this.preprocessorExtension = extension;
    return this;
  }

  /**
   * Get the current preprocessor extension
   *
   * @return the current preprocessor extension, it can be null
   * @see PreprocessorExtension
   */
  public PreprocessorExtension getPreprocessorExtension() {
    return preprocessorExtension;
  }

  /**
   * Set the character encoding for reading texts, it must be supported by the
   * Java platform else an exception will be thrown
   *
   * @param characterEncoding a character encoding as a String, it must not be
   * null and must be supported by the Java platform
   * @return this preprocessor context
   */
  public PreprocessorContext setInCharacterEncoding(final String characterEncoding) {
    PreprocessorUtils.assertNotNull("Value is null", characterEncoding);

    if (!Charset.isSupported(characterEncoding)) {
      throw makeException("Unsupported character encoding [" + characterEncoding + ']', null);
    }
    this.inCharacterEncoding = characterEncoding;
    return this;
  }

  /**
   * Set the output texts character encoding, it must be supported by the Java
   * platform else an exception will be thrown
   *
   * @param characterEncoding a character encoding as a String, it must not be
   * null and must be supported by the Java platform
   * @return this preprocessor context
   */
  public PreprocessorContext setOutCharacterEncoding(final String characterEncoding) {
    if (!Charset.isSupported(characterEncoding)) {
      throw makeException("Unsupported character encoding [" + characterEncoding + ']', null);
    }
    this.outCharacterEncoding = characterEncoding;
    return this;
  }

  /**
   * Get the current character encoding for text reading
   *
   * @return the current read texts character encoding as a String
   */
  public String getInCharacterEncoding() {
    return inCharacterEncoding;
  }

  /**
   * Get the current character encoding for text writing
   *
   * @return the current text writing character encoding as a String
   */
  public String getOutCharacterEncoding() {
    return outCharacterEncoding;
  }

  /**
   * It allows to create a File object for its path subject to the destination
   * directory path
   *
   * @param path the path to the file, it must not be null
   * @return a generated File object for the path
   */
  public File createDestinationFileForPath(final String path) {
    PreprocessorUtils.assertNotNull("Path is null", path);

    if (path.isEmpty()) {
      throw makeException("File name is empty",null);
    }

    return new File(getDestinationDirectoryAsFile(), path);
  }

  /**
   * It returns a File object for a path to a source file subject to the source
   * directory path
   *
   * @param path the path to the needed source file, it must not be null and the
   * file must exist and be a file
   * @return a generated File object for the path
   * @throws IOException it will be thrown for problem to create the File or to
   * find it on the disk
   */
  public File getSourceFile(final String path) throws IOException {
    if (path == null) {
      throw makeException("Path is null", null);
    }

    if (path.isEmpty()) {
      throw makeException("Folder path is empty", null);
    }

    File result = null;

    String parentDir = null;
    if (currentState != null && currentState.peekFile() != null) {
      parentDir = currentState.peekFile().getFile().getParent();
    }

    if (FilenameUtils.getPrefixLength(path) <= 0 && parentDir != null) {
      // relative path
      result = new File(parentDir, path);
    }
    else {
      final List<File> findFiles = new ArrayList<File>();
      for (final File root : getSourceDirectoryAsFiles()) {
        final File variant = new File(root, path);
        if (variant.exists() && variant.isFile()) {
          findFiles.add(variant);
        }
      }

      if (findFiles.size() == 1) {
        result = findFiles.get(0);
      }
      else if (findFiles.isEmpty()) {
        result = null;
      }
      else {
        throw makeException("Found several variants for path \'" + path + "\' in different source roots",null);
      }
    }

    if (result == null || !result.isFile()) {
      throw makeException("The File \'" + PreprocessorUtils.getFilePath(result) + "\' is not found or not a file",null);
    }
    return result;
  }

  /**
   * Add a configuration file, it is a file which contains directives and global
   * variable definitions
   *
   * @param file a file, it must not be null
   */
  public void addConfigFile(final File file) {
    PreprocessorUtils.assertNotNull("File is null", file);
    configFiles.add(file);
  }

  /**
   * Get array of current registered configuration files
   *
   * @return a file array contains the current registered configuration files
   */
  public File[] getConfigFiles() {
    return configFiles.toArray(new File[configFiles.size()]);
  }

  /**
   * Generate new preprocessing state object, also the new preprocessing state
   * will be saved as the current one in the context
   *
   * @param fileContainer a file container which will be using the preprocessor
   * state, it must not be null
   * @param  phaseIndex  index of phase (0 - global, 1 - preprocessing)
   * @return new generated preprocessor state
   * @throws IOException it will be throws if there is any error in opening and
   * reading operations
   */
  public PreprocessingState produceNewPreprocessingState(final FileInfoContainer fileContainer, final int phaseIndex) throws IOException {
    PreprocessorUtils.assertNotNull("File container is null", fileContainer);

    if (verbose) {
      if (phaseIndex == 0){
      logInfo("Start search global definitions in '" + PreprocessorUtils.getFilePath(fileContainer.getSourceFile()) + '\'');
      }else{
        logInfo("Start preprocessing '" + PreprocessorUtils.getFilePath(fileContainer.getSourceFile()) + '\'');
      }
    }
    this.currentState = new PreprocessingState(fileContainer, getInCharacterEncoding(), getOutCharacterEncoding());
    return this.currentState;
  }

  /**
   * Generate new preprocessing state for a file container and a text container,
   * also the new preprocessing state will be saved as the current one in the
   * context
   *
   * @param fileContainer the file container to be used to create the new
   * preprocessing state, it must not be null
   * @param textContainer the text container to be used to create the new
   * preprocessing state, it must not be null
   * @return new generated preprocessing state
   */
  public PreprocessingState produceNewPreprocessingState(final FileInfoContainer fileContainer, final TextFileDataContainer textContainer) {
    this.currentState = new PreprocessingState(fileContainer, textContainer, getInCharacterEncoding(), getOutCharacterEncoding());
    return this.currentState;
  }

  /**
   * Get the last generated preprocessing state, it is the current one
   *
   * @return the last generated preprocessing state or null if there is not
   * anyone
   */
  public PreprocessingState getPreprocessingState() {
    return this.currentState;
  }

  /**
   * Prepare exception with message and cause, or return cause if it is a
   * preprocessor exception
   *
   * @param text the message text, must not be null
   * @param cause the cause, it can be null
   * @return prepared exception with additional information
   */
  public PreprocessorException makeException(final String text, final Throwable cause) {
    if (cause != null && cause instanceof PreprocessorException) {
      return (PreprocessorException) cause;
    }

    final FilePositionInfo[] includeStack;
    final String sourceLine;
    if (this.currentState == null) {
      includeStack = PreprocessingState.EMPTY_STACK;
      sourceLine = "";
    }
    else {
      includeStack = this.currentState.makeIncludeStack();
      sourceLine = this.currentState.getLastReadString();
    }
    return new PreprocessorException(text, sourceLine, includeStack, cause);
  }

  public void logForVerbose(final String str) {
    if (isVerbose()) {
      final String stack;
      if (this.currentState!=null){
        stack = makeStackView(this.currentInCloneSource, this.cloned, this.currentState.getCurrentIncludeStack());
      }else{
        stack = "";
      }
      this.logInfo(str + (stack.isEmpty() ? ' ' : '\n') + stack);
    }
  }

  private static String makeStackView(final TextFileDataContainer cloneSource, final boolean cloned, final List<TextFileDataContainer> list) {
    if (list == null || list.isEmpty()) return "";
    final StringBuilder builder = new StringBuilder();
    int tab = 5;
    
    for (int s = 0; s < tab; s++) {
      builder.append(' ');
    }
    
    builder.append('{');
    if (cloned){
      builder.append(cloneSource == null ? "*No src info" : "*" + cloneSource.getFile().getName()+':'+cloneSource.getNextStringIndex());
    }else{
      builder.append("File chain");
    }
    builder.append('}');
    tab +=5;
    
    int fileIndex = 1;
    for (int i = list.size() - 1; i >= 0; i--) {
      final TextFileDataContainer cur = list.get(i);
      builder.append('\n');
      for (int s = 0; s < tab; s++) {
        builder.append(' ');
      }
      builder.append("└>");
      builder.append(fileIndex++).append(". ");
      builder.append(cur.getFile().getName()).append(':').append(cur.getLastReadStringIndex() + 1);
      tab += 3;
   }

    return builder.toString();
  }
}
