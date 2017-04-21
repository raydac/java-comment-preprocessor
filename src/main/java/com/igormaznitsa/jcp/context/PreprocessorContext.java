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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;

import com.igormaznitsa.meta.annotation.MustNotContainNull;
import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;
import com.igormaznitsa.meta.common.utils.Assertions;

/**
 * The preprocessor context class is a main class which contains all options of the preprocessor and allows to work with variables in expressions
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class PreprocessorContext {

  public static final String DEFAULT_SOURCE_DIRECTORY = "." + File.separatorChar;
  public static final String DEFAULT_DEST_DIRECTORY = ".." + File.separatorChar + "preprocessed";
  public static final String DEFAULT_PROCESSING_EXTENSIONS = "java,txt,htm,html";
  public static final String DEFAULT_EXCLUDED_EXTENSIONS = "xml";
  public static final String DEFAULT_CHARSET = "UTF8";

  private boolean verbose = false;
  private boolean removeComments = false;
  private boolean clearDestinationDirectoryBefore = false;
  private boolean fileOutputDisabled = false;
  private boolean keepNonExecutingLines = false;
  private boolean careForLastNextLine = false;
  private boolean compareDestination = false;
  private boolean allowWhitespace = false;
  private boolean preserveIndent = false;
  private boolean copyFileAttributes = false;
  private boolean unknownVariableAsFalse = false;
  
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

  private String [] excludedFolderPatterns = new String[0];
  
  /**
   * The constructor
   */
  public PreprocessorContext() {
    this.currentState = new PreprocessingState(this, this.inCharacterEncoding, this.outCharacterEncoding);
    setSourceDirectories(DEFAULT_SOURCE_DIRECTORY).setDestinationDirectory(DEFAULT_DEST_DIRECTORY);
    registerSpecialVariableProcessor(new JCPSpecialVariableProcessor());
    registerSpecialVariableProcessor(new EnvironmentVariableProcessor());
    this.cloned = false;
    this.currentInCloneSource = null;
  }

  /**
   * Set patterns for excluded folders.
   * @param patterns array contains Ant path patterns
   */
  public void setExcludedFolderPatterns(@MustNotContainNull @Nonnull final String ... patterns) {
    final String [] value = Assertions.assertDoesntContainNull(Assertions.assertNotNull(patterns));
    final String [] normalized = new String[value.length];
    for(int i=0;i<value.length;i++){
      normalized[i] = FilenameUtils.normalize(value[i],true);
    }
    this.excludedFolderPatterns = normalized;
  }
  
  /**
   * Get patterns for excluded folders.
   * @return array of patterns in Ant pattern format
   */
  @Nonnull
  @MustNotContainNull
  public String [] getExcludedFolderPatterns() {
    return this.excludedFolderPatterns.clone();
  }
  
  /**
   * Set the flag to care to be precise in processing the last file next line char
   *
   * @param flag true to turn on the mode, false to turn off
   */
  public void setCareForLastNextLine(final boolean flag) {
    this.careForLastNextLine = flag;
  }

  public boolean isCareForLastNextLine() {
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
  public PreprocessorContext(@Nonnull final PreprocessorContext context) {
    assertNotNull("Source context must not be null", context);

    this.verbose = context.verbose;
    this.removeComments = context.removeComments;
    this.clearDestinationDirectoryBefore = context.clearDestinationDirectoryBefore;
    this.fileOutputDisabled = context.fileOutputDisabled;
    this.keepNonExecutingLines = context.keepNonExecutingLines;
    this.allowWhitespace = context.allowWhitespace;
    this.preserveIndent = context.preserveIndent;
    this.sourceDirectories = context.sourceDirectories;
    this.destinationDirectory = context.destinationDirectory;
    this.destinationDirectoryFile = context.destinationDirectoryFile;
    this.sourceDirectoryFiles = context.sourceDirectoryFiles.clone();
    this.copyFileAttributes = context.copyFileAttributes;
    this.careForLastNextLine = context.careForLastNextLine;

    this.processingFileExtensions.clear();
    this.processingFileExtensions.addAll(context.processingFileExtensions);

    this.excludedFileExtensions.clear();
    this.excludedFileExtensions.addAll(context.excludedFileExtensions);

    this.unknownVariableAsFalse = context.unknownVariableAsFalse;
    
    this.preprocessorExtension = context.preprocessorExtension;
    this.inCharacterEncoding = context.inCharacterEncoding;
    this.outCharacterEncoding = context.outCharacterEncoding;
    this.compareDestination = context.compareDestination;

    this.globalVarTable.putAll(context.globalVarTable);
    this.localVarTable.putAll(context.localVarTable);
    this.excludedFolderPatterns = context.excludedFolderPatterns.clone();
    
    this.mapVariableNameToSpecialVarProcessor.putAll(context.mapVariableNameToSpecialVarProcessor);
    this.sharedResources.putAll(context.sharedResources);

    this.configFiles.addAll(context.configFiles);

    this.currentState = assertNotNull(context.currentState);
    this.cloned = true;
    
    this.preprocessorLogger = context.preprocessorLogger;
    
    final PreprocessingState theState = context.getPreprocessingState();
    this.currentInCloneSource = theState.peekFile();
  }

  /**
   * Set the logger to print information and error messages
   *
   * @param logger a logger to be used for output, it can be null
   */
  public void setPreprocessorLogger(@Nullable final PreprocessorLogger logger) {
    preprocessorLogger = logger;
  }

  /**
   * It allows to register a special variable processor which can process some special global variables
   *
   * @param processor a variable processor to be registered, it must not be null
   * @see SpecialVariableProcessor
   */
  public void registerSpecialVariableProcessor(@Nonnull final SpecialVariableProcessor processor) {
    assertNotNull("Processor is null", processor);

    for (final String varName : processor.getVariableNames()) {
      assertNotNull("A Special Var name is null", varName);
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
  public void logInfo(@Nullable final String text) {
    if (text != null && this.preprocessorLogger != null) {
      this.preprocessorLogger.info(text);
    }
  }

  /**
   * Print an information about an error into the current log
   *
   * @param text a String to be printed into the error log, it can be null
   */
  public void logError(@Nullable final String text) {
    if (text != null && this.preprocessorLogger != null) {
      this.preprocessorLogger.error(text);
    }
  }

  /**
   * Print some debug info into the current log
   *
   * @param text a String to be printed into the error log, it can be null
   * @since 6.0.1
   */
  public void logDebug(@Nullable final String text) {
    if (text != null && this.preprocessorLogger != null) {
      this.preprocessorLogger.debug(text);
    }
  }

  /**
   * Print an information about a warning situation into the current log
   *
   * @param text a String to be printed into the warning log, it can be null
   */
  public void logWarning(@Nullable final String text) {
    if (text != null || this.preprocessorLogger != null) {
      this.preprocessorLogger.warning(text);
    }
  }

  /**
   * Set the remove comments flag
   *
   * @param removingComments the flag to set, true if comments must be removed from the result files, otherwise else
   * @return the preprocessor context instance
   */
  @Nonnull
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
   * @param flag true if preprocessor must not make any writing operations, otherwise false
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
   * Set flag to allow whitespace between directive and comment chars.
   * @param flag true if whitespace allowed, false otherwise
   */
  public void setAllowWhitespace(final boolean flag){
    this.allowWhitespace = flag;
  }
  
  /**
   * Get flag that whitespace allowed between directive and comment.
   * @return true if whitespace allowed, false otherwise
   */
  public boolean isAllowWhitespace() {
    return this.allowWhitespace;
  }
  
  /**
   * Set flag to interpret unknown variable value as FALSE.
   * @param flag true to turn on mode when unknown variable will be recognized as FALSE
   */
  public void setUnknownVariableAsFalse(final boolean flag) {
    this.unknownVariableAsFalse = flag;
  }
  
  /**
   * Get flag shows that unknown variable is recognized as FALSE.
   * @return true if unknown variable must be recognized as FALSE.
   */
  public boolean isUnknownVariableAsFalse() {
    return this.unknownVariableAsFalse;
  }
  
  /**
   * Set flag to control whether prefixes "//$", "//$$" should replaced
   * with equal length whitespace strings rather than just removed.
   * @param flag true enables preserve-indent, false disables it
   */
  public void setPreserveIndent(final boolean flag){
    this.preserveIndent = flag;
  }

  /**
   * Get flag indicating whether preserve-indent is enabled or disabled.
   * @return true if preserve-indent is enabled, false otherwise
   */
  public boolean isPreserveIndent() {
    return this.preserveIndent;
  }

  /**
   * Set source directories
   *
   * @param directories semi separated list of source directories, must not be null
   * @return this preprocessor context instance
   */
  @Nonnull
  public PreprocessorContext setSourceDirectories(@Nonnull final String directories) {
    assertNotNull("Source directory is null", directories);

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
  public void setSharedResource(@Nonnull final String name, @Nonnull final Object obj) {
    assertNotNull("Name is null", name);
    assertNotNull("Object is null", obj);

    sharedResources.put(name, obj);
  }

  /**
   * Get a shared source from inside map
   *
   * @param name the name of the needed object, it must not be null
   * @return a cached object or null if it is not found
   */
  @Nullable
  public Object getSharedResource(@Nonnull final String name) {
    assertNotNull("Name is null", name);
    return sharedResources.get(name);
  }

  /**
   * Remove a shared object from the inside map for its name
   *
   * @param name the object name, it must not be null
   * @return removing object or null if it is not found
   */
  @Nullable
  public Object removeSharedResource(@Nonnull final String name) {
    assertNotNull("Name is null", name);
    return sharedResources.remove(name);
  }

  /**
   * Get the source directories as semi separated string
   *
   * @return the current source directories semi separated list
   */
  @Nonnull
  public String getSourceDirectories() {
    return sourceDirectories;
  }

  /**
   * Get the current source directories as a file array
   *
   * @return the current source directories as a file array
   */
  @Nonnull
  @MustNotContainNull
  public File[] getSourceDirectoryAsFiles() {
    return sourceDirectoryFiles;
  }

  /**
   * Inside auxiliary method to parse the source directories list into file array
   *
   * @return parsed file list, each file must exist and be a directory
   */
  @Nonnull
  @MustNotContainNull
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
  @Nonnull
  public PreprocessorContext setDestinationDirectory(@Nonnull final String directory) {
    assertNotNull("Directory is null", directory);
    this.destinationDirectory = directory;
    destinationDirectoryFile = new File(this.destinationDirectory);

    return this;
  }

  /**
   * Get the current destination directory as a File object
   *
   * @return the current destination directory as an object
   */
  @Nonnull
  public File getDestinationDirectoryAsFile() {
    return destinationDirectoryFile;
  }

  /**
   * Get the string representation of the destination directory
   *
   * @return the current destination directory as a String
   */
  @Nonnull
  public String getDestinationDirectory() {
    return destinationDirectory;
  }

  /**
   * Set file extensions of files to be preprocessed, it is a comma separated list
   *
   * @param extensions comma separated extensions list of file extensions to be preprocessed, must not be null
   * @return this preprocessor context
   */
  @Nonnull
  public PreprocessorContext setProcessingFileExtensions(@Nonnull final String extensions) {
    assertNotNull("Argument is null", extensions);
    processingFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.splitExtensionCommaList(extensions)));
    return this;
  }

  /**
   * Get file extensions of files to be preprocessed as a string array
   *
   * @return a string array of file extensions to be preprocessed
   */
  @Nonnull
  @MustNotContainNull
  public String[] getProcessingFileExtensions() {
    return processingFileExtensions.toArray(new String[processingFileExtensions.size()]);
  }

  /**
   * Check that a file is allowed to be preprocessed fo its extension
   *
   * @param file a file to be checked
   * @return true if the file is allowed, false otherwise
   */
  public final boolean isFileAllowedToBeProcessed(@Nullable final File file) {
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
  public final boolean isFileExcludedFromProcess(@Nullable final File file) {
    final boolean result;
    if (file != null && file.isFile()) {
      result = excludedFileExtensions.contains(PreprocessorUtils.getFileExtension(file));
    } else {
      result = false;
    }
    return result;
  }

  /**
   * Set comma separated list of file extensions to be excluded from preprocessing
   *
   * @param extensions a comma separated file extension list, it must not be null
   * @return this preprocessor context
   */
  @Nonnull
  public PreprocessorContext setExcludedFileExtensions(@Nonnull final String extensions) {
    assertNotNull("Argument is null", extensions);
    excludedFileExtensions = new HashSet<String>(Arrays.asList(PreprocessorUtils.splitExtensionCommaList(extensions)));
    return this;
  }

  /**
   * Get excluded file extension list as a string array
   *
   * @return a string array contains file extensions to be excluded from preprocessing act
   */
  @Nonnull
  @MustNotContainNull
  public String[] getExcludedFileExtensions() {
    return excludedFileExtensions.toArray(new String[excludedFileExtensions.size()]);
  }

  /**
   * Set the flag to clear the destination directory before preprocessing
   *
   * @param flag true if the directory must be cleaned, otherwise false
   * @return this preprocessor context
   */
  @Nonnull
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
   * @param name the variable name, must not be null, remember that the name will be normalized and will be entirely in lower case
   * @param value the value for the variable, it must not be null
   * @return this preprocessor context
   * @see Value
   */
  @Nonnull
  public PreprocessorContext setLocalVariable(@Nonnull final String name, @Nonnull final Value value) {
    assertNotNull("Variable name is null", name);
    assertNotNull("Value is null", value);

    final String normalized = assertNotNull(PreprocessorUtils.normalizeVariableName(name));

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
   * @param name the variable name, must not be null, remember that the name will be normalized and will be entirely in lower case
   * @return this preprocessor context
   * @see Value
   */
  @Nonnull
  public PreprocessorContext removeLocalVariable(@Nonnull final String name) {
    assertNotNull("Variable name is null", name);
    final String normalized = assertNotNull(PreprocessorUtils.normalizeVariableName(name));

    if (normalized.isEmpty()) {
      throw makeException("Empty variable name", null);
    }

    if (mapVariableNameToSpecialVarProcessor.containsKey(normalized) || globalVarTable.containsKey(normalized)) {
      throw makeException("Attempting to remove either a global variable or a special variable as a local one [" + normalized + ']', null);
    }

    if (isVerbose()) {
      logForVerbose("Removing local variable '" + normalized + "\'");
    }
    localVarTable.remove(normalized);
    return this;
  }

  /**
   * Remove a global variable value from the context.
   *
   * @param name the variable name, must not be null, remember that the name will be normalized and will be entirely in lower case
   * @return this preprocessor context
   * @see Value
   */
  @Nonnull
  public PreprocessorContext removeGlobalVariable(@Nonnull final String name) {
    assertNotNull("Variable name is null", name);

    final String normalized = assertNotNull(PreprocessorUtils.normalizeVariableName(name));

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
   * @param name the name for the variable, it can be null. The name will be normalized to allowed one.
   * @return null either if the name is null or the variable is not found, otherwise its value
   */
  @Nullable
  public Value getLocalVariable(@Nullable final String name) {
    if (name == null) {
      return null;
    }

    final String normalized = assertNotNull(PreprocessorUtils.normalizeVariableName(name));

    if (normalized.isEmpty()) {
      return null;
    }

    return localVarTable.get(normalized);
  }

  /**
   * Check that a local variable for a name is presented
   *
   * @param name the checking name, it will be normalized to the support format and can be null
   * @return false either if the name is null or there is not any local variable for the name, otherwise true
   */
  public boolean containsLocalVariable(@Nullable final String name) {
    if (name == null) {
      return false;
    }

    final String normalized = assertNotNull(PreprocessorUtils.normalizeVariableName(name));

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
  @Nonnull
  public PreprocessorContext clearLocalVariables() {
    localVarTable.clear();
    return this;
  }

  /**
   * Set a global variable value
   *
   * @param name the variable name, it must not be null and will be normalized to the supported format
   * @param value the variable value, it must not be null
   * @return this preprocessor context
   */
  @Nonnull
  public PreprocessorContext setGlobalVariable(@Nonnull final String name, @Nonnull final Value value) {
    assertNotNull("Variable name is null", name);

    final String normalizedName = assertNotNull(PreprocessorUtils.normalizeVariableName(name));

    if (normalizedName.isEmpty()) {
      throw makeException("Name is empty", null);
    }

    assertNotNull("Value is null", value);

    if (mapVariableNameToSpecialVarProcessor.containsKey(normalizedName)) {
      mapVariableNameToSpecialVarProcessor.get(normalizedName).setVariable(normalizedName, value, this);
    } else {
      if (isVerbose()) {
        final String valueAsStr = value.toString();
        if (globalVarTable.containsKey(normalizedName)) {
          logForVerbose("Replacing global variable [" + normalizedName + '=' + valueAsStr + ']');
        } else {
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
   * @param name the checking name, it will be normalized to the supported format, it can be null
   * @return true if such variable is presented for its name in the inside storage, otherwise false (also it is false if the name is null)
   */
  public boolean containsGlobalVariable(@Nullable final String name) {
    if (name == null) {
      return false;
    }

    final String normalized = assertNotNull(PreprocessorUtils.normalizeVariableName(name));
    if (normalized.isEmpty()) {
      return false;
    }

    return mapVariableNameToSpecialVarProcessor.containsKey(normalized) || globalVarTable.containsKey(normalized);
  }

  /**
   * Find value among local and global variables for a name. It finds in the order: special processors, local variables, global variables
   *
   * @param name the name for the needed variable, it will be normalized to the supported format
   * @param enforceUnknownVarAsNull if true then state of the unknownVariableAsFalse flag in context will be ignored
   * @return false if either the variable is not found or the name is null, otherwise the variable value
   */
  @Nullable
  public Value findVariableForName(@Nullable final String name, final boolean enforceUnknownVarAsNull) {
    if (name == null) {
      return null;
    }

    final String normalized = assertNotNull(PreprocessorUtils.normalizeVariableName(name));

    if (normalized.isEmpty()) {
      return null;
    }

    final SpecialVariableProcessor processor = mapVariableNameToSpecialVarProcessor.get(normalized);

    if (processor != null) {
      return processor.getVariable(normalized, this);
    }

    final Value val = getLocalVariable(normalized);
    if (val != null) {
      return val;
    }

    Value result = globalVarTable.get(normalized);
  
    if (result == null && !enforceUnknownVarAsNull && this.unknownVariableAsFalse) {
      logDebug("Unknown variable '"+name+"' is replaced by FALSE!");
      result = Value.BOOLEAN_FALSE;
    }
    
    return result;
  }

  /**
   * Check that there is a global variable with such name.
   *
   * @param variableName a name to be checked, can be null
   * @return false if there is not such variable or it is null, true if such global or special variable exists
   */
  public boolean isGlobalVariable(@Nullable final String variableName) {
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
   * @return false if there is not such variable or it is null, true if such local variable exists
   */
  public boolean isLocalVariable(@Nullable final String variableName) {
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
  @Nonnull
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
   * Set the flag to check before saving if the content changed.
   *
   * @param flag true if to check, false otherwise
   * @return the preprocessor context
   */
  @Nonnull
  public PreprocessorContext setCompareDestination(final boolean flag) {
    this.compareDestination = flag;
    return this;
  }

  /**
   * Check the flag to check content of existing file before saving.
   *
   * @return true if the content should be checked and new content must not be replaced if it is the same
   */
  public boolean isCompareDestination() {
    return this.compareDestination;
  }

  /**
   * Set the flag to keep lines as commented ones
   *
   * @param flag true if the preprocessor must keep non-executing lines, otherwise false
   * @return this preprocessor context
   */
  @Nonnull
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
   * Check that the preprocessor must copy file attributes.
   * 
   * @return true if the preprocessor must copy file attributes, false otherwise.
   */
  public boolean isCopyFileAttributes() {
    return this.copyFileAttributes;
  }
  
  /**
   * Set the flag to copy file attributes.
   * @param value true if file attributes must be copied, false otherwise.
   * @return the preprocessor context
   */
  @Nonnull
  public PreprocessorContext setCopyFileAttributes(final boolean value) {
    this.copyFileAttributes = value;
    return this;
  }
  
  /**
   * Set a preprocessor extension, it is a module implements the PreprocessorExtension interface which can process and get some calls from a preprocessor during its work
   *
   * @param extension an object implements the PreprocessorExtension interface, it can be null
   * @return this preprocessor context
   * @see PreprocessorExtension
   */
  @Nonnull
  public PreprocessorContext setPreprocessorExtension(@Nullable final PreprocessorExtension extension) {
    this.preprocessorExtension = extension;
    return this;
  }

  /**
   * Get the current preprocessor extension
   *
   * @return the current preprocessor extension, it can be null
   * @see PreprocessorExtension
   */
  @Nullable
  public PreprocessorExtension getPreprocessorExtension() {
    return preprocessorExtension;
  }

  /**
   * Set the character encoding for reading texts, it must be supported by the Java platform else an exception will be thrown
   *
   * @param characterEncoding a character encoding as a String, it must not be null and must be supported by the Java platform
   * @return this preprocessor context
   */
  @Nonnull
  public PreprocessorContext setInCharacterEncoding(@Nonnull final String characterEncoding) {
    assertNotNull("Value is null", characterEncoding);

    if (!Charset.isSupported(characterEncoding)) {
      throw makeException("Unsupported character encoding [" + characterEncoding + ']', null);
    }
    this.inCharacterEncoding = characterEncoding;
    return this;
  }

  /**
   * Set the output texts character encoding, it must be supported by the Java platform else an exception will be thrown
   *
   * @param characterEncoding a character encoding as a String, it must not be null and must be supported by the Java platform
   * @return this preprocessor context
   */
  @Nonnull
  public PreprocessorContext setOutCharacterEncoding(@Nonnull final String characterEncoding) {
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
  @Nonnull
  public String getInCharacterEncoding() {
    return inCharacterEncoding;
  }

  /**
   * Get the current character encoding for text writing
   *
   * @return the current text writing character encoding as a String
   */
  @Nonnull
  public String getOutCharacterEncoding() {
    return outCharacterEncoding;
  }

  /**
   * It allows to create a File object for its path subject to the destination directory path
   *
   * @param path the path to the file, it must not be null
   * @return a generated File object for the path
   */
  @Nonnull
  public File createDestinationFileForPath(@Nonnull final String path) {
    assertNotNull("Path is null", path);

    if (path.isEmpty()) {
      throw makeException("File name is empty", null);
    }

    return new File(getDestinationDirectoryAsFile(), path);
  }

  /**
   * It finds a file for its path among files in source folder, it is prohibited to return files out of preprocessing folders.
   *
   * @param path the path to the needed file, it must not be null and the file must exist and be a file and be among files in preprocessing source folders
   * @return detected file object for the path
   * @throws IOException if it is impossible to find a file for the path
   */
  @Nonnull
  public File findFileInSourceFolder(@Nonnull final String path) throws IOException {
    if (path == null) {
      throw makeException("Path is null", null);
    }

    if (path.isEmpty()) {
      throw makeException("Path is empty", null);
    }

    File result = null;

    final TextFileDataContainer theFile = currentState.peekFile();
    final String parentDir = theFile == null ? null : theFile.getFile().getParent();

    final File resultFile = new File(path);
    if (resultFile.isAbsolute()) {
      // absolute path

      // check that the file is a child of a preprocessing source root else usage of the file is prohibited
      final String normalizedPath = FilenameUtils.normalizeNoEndSeparator(resultFile.getAbsolutePath());
      for (final File root : getSourceDirectoryAsFiles()) {
        final String rootNormalizedPath = FilenameUtils.normalizeNoEndSeparator(root.getAbsolutePath()) + File.separatorChar;
        if (normalizedPath.startsWith(rootNormalizedPath)) {
          result = resultFile;
          break;
        }
      }

      if (result == null) {
        throw makeException("Can't find file for path \'" + path + "\' in preprocessing source folders, allowed usage only files in preprocessing source folders!", null);
      } else if (!result.isFile()) {
        throw makeException("File \'" + result + "\' is either not found or not a file", null);
      }

    } else if (parentDir != null) {
      // relative path
      result = new File(parentDir, path);
    } else {
      final List<File> setOfFoundFiles = new ArrayList<File>();
      for (final File root : getSourceDirectoryAsFiles()) {
        final File variant = new File(root, path);
        if (variant.exists() && variant.isFile()) {
          setOfFoundFiles.add(variant);
        }
      }

      if (setOfFoundFiles.size() == 1) {
        result = setOfFoundFiles.get(0);
      } else if (setOfFoundFiles.isEmpty()) {
        result = null;
      } else {
        throw makeException("Found several variants for path \'" + path + "\' in different source roots", null);
      }

      if (result == null) {
        throw makeException("Can't find file for path \'" + path + "\' among source files registered for preprocessing.", null);
      } else if (!result.isFile()) {
        throw makeException("File \'" + PreprocessorUtils.getFilePath(result) + "\' is either not found or not a file", null);
      }
    }

    return result;
  }

  /**
   * Add a configuration file, it is a file which contains directives and global variable definitions
   *
   * @param file a file, it must not be null
   */
  public void addConfigFile(@Nonnull final File file) {
    assertNotNull("File is null", file);
    configFiles.add(file);
  }

  /**
   * Get array of current registered configuration files
   *
   * @return a file array contains the current registered configuration files
   */
  @Nonnull
  @MustNotContainNull
  public File[] getConfigFiles() {
    return configFiles.toArray(new File[configFiles.size()]);
  }

  /**
   * Generate new preprocessing state object, also the new preprocessing state will be saved as the current one in the context
   *
   * @param fileContainer a file container which will be using the preprocessor state, it must not be null
   * @param phaseIndex index of phase (0 - global, 1 - preprocessing)
   * @return new generated preprocessor state
   * @throws IOException it will be throws if there is any error in opening and reading operations
   */
  @Nonnull
  public PreprocessingState produceNewPreprocessingState(@Nonnull final FileInfoContainer fileContainer, final int phaseIndex) throws IOException {
    assertNotNull("File container is null", fileContainer);

    if (verbose) {
      if (phaseIndex == 0) {
        logInfo("Start search global definitions in '" + PreprocessorUtils.getFilePath(fileContainer.getSourceFile()) + '\'');
      } else {
        logInfo("Start preprocessing '" + PreprocessorUtils.getFilePath(fileContainer.getSourceFile()) + '\'');
      }
    }
    this.currentState = new PreprocessingState(this, fileContainer, getInCharacterEncoding(), getOutCharacterEncoding(), this.compareDestination);
    return this.currentState;
  }

  /**
   * Generate new preprocessing state for a file container and a text container, also the new preprocessing state will be saved as the current one in the context
   *
   * @param fileContainer the file container to be used to create the new preprocessing state, it must not be null
   * @param textContainer the text container to be used to create the new preprocessing state, it must not be null
   * @return new generated preprocessing state
   */
  @Nonnull
  public PreprocessingState produceNewPreprocessingState(@Nonnull final FileInfoContainer fileContainer, @Nonnull final TextFileDataContainer textContainer) {
    this.currentState = new PreprocessingState(this, fileContainer, textContainer, getInCharacterEncoding(), getOutCharacterEncoding(), this.compareDestination);
    return this.currentState;
  }

  /**
   * Get the last generated preprocessing state, it is the current one
   *
   * @return the last generated preprocessing state
   */
  @Nonnull
  public PreprocessingState getPreprocessingState() {
    return this.currentState;
  }

  /**
   * Prepare exception with message and cause, or return cause if it is a preprocessor exception
   *
   * @param text the message text, must not be null
   * @param cause the cause, it can be null
   * @return prepared exception with additional information
   */
  @Nonnull
  public PreprocessorException makeException(@Nonnull final String text, @Nullable final Throwable cause) {
    if (cause != null && cause instanceof PreprocessorException) {
      return (PreprocessorException) cause;
    }

    final FilePositionInfo[] includeStack;
    final String sourceLine;
    includeStack = this.currentState.makeIncludeStack();
    sourceLine = this.currentState.getLastReadString();
    return new PreprocessorException(text, sourceLine, includeStack, cause);
  }

  public void logForVerbose(@Nonnull final String str) {
    if (isVerbose()) {
      final String stack;
      stack = makeStackView(this.currentInCloneSource, this.cloned, this.currentState.getCurrentIncludeStack());
      this.logInfo(str + (stack.isEmpty() ? ' ' : '\n') + stack);
    }
  }

  @Nonnull
  private static String makeStackView(@Nullable final TextFileDataContainer cloneSource, final boolean cloned, @Nullable @MustNotContainNull final List<TextFileDataContainer> list) {
    if (list == null || list.isEmpty()) {
      return "";
    }
    final StringBuilder builder = new StringBuilder();
    int tab = 5;

    for (int s = 0; s < tab; s++) {
      builder.append(' ');
    }

    builder.append('{');
    if (cloned) {
      builder.append(cloneSource == null ? "*No src info" : "*" + cloneSource.getFile().getName() + ':' + cloneSource.getNextStringIndex());
    } else {
      builder.append("File chain");
    }
    builder.append('}');
    tab += 5;

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
