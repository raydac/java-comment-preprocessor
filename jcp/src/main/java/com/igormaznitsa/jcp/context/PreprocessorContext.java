/*
 * Copyright 2002-2019 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
import com.igormaznitsa.meta.annotation.MustNotContainNull;
import com.igormaznitsa.meta.common.utils.GetUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.igormaznitsa.meta.common.utils.Assertions.assertDoesntContainNull;
import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

/**
 * Preprocessor context class is a main class which contains all options for preprocessin and allow to work with variables in expressions.
 */
@Data
public class PreprocessorContext {

  public static final List<String> DEFAULT_SOURCE_DIRECTORY = Collections.singletonList("." + File.separatorChar);
  public static final String DEFAULT_DEST_DIRECTORY = ".." + File.separatorChar + "preprocessed";
  public static final List<String> DEFAULT_PROCESSING_EXTENSIONS = unmodifiableList(asList("java", "txt", "htm", "html"));
  public static final List<String> DEFAULT_EXCLUDED_EXTENSIONS = singletonList("xml");
  public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  private final Map<String, Value> globalVarTable = new HashMap<>();
  private final Map<String, Value> localVarTable = new HashMap<>();
  private final Map<String, SpecialVariableProcessor> mapVariableNameToSpecialVarProcessor = new HashMap<>();
  private final Map<String, Object> sharedResources = new HashMap<>();
  private final List<File> configFiles = new ArrayList<>();

  @Setter(AccessLevel.NONE)
  private final boolean cloned;
  @Setter(AccessLevel.NONE)
  private final TextFileDataContainer currentInCloneSource;
  private final List<SourceFolder> sources = new ArrayList<>();
  private final File baseDir;
  private String eol = GetUtils.ensureNonNull(System.getProperty("jcp.line.separator", System.getProperty("line.separator")), "\n");
  private boolean verbose = false;
  private boolean keepComments = false;
  private boolean clearTarget = false;
  private boolean dryRun = false;
  private boolean keepLines = false;
  private boolean careForLastEol = false;
  private boolean dontOverwriteSameContent = false;
  private boolean allowWhitespaces = false;
  private boolean preserveIndents = false;
  private boolean keepAttributes = false;
  private boolean unknownVariableAsFalse = false;
  private File target;
  private Set<String> extensions = new HashSet<>(DEFAULT_PROCESSING_EXTENSIONS);
  private Set<String> excludeExtensions = new HashSet<>(DEFAULT_EXCLUDED_EXTENSIONS);
  private PreprocessorExtension preprocessorExtension;
  private Charset sourceEncoding = DEFAULT_CHARSET;
  private Charset targetEncoding = DEFAULT_CHARSET;
  @Setter(AccessLevel.NONE)
  private PreprocessorLogger preprocessorLogger = new SystemOutLogger();

  private List<String> excludeFolders = new ArrayList<>();

  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  private final AtomicReference<PreprocessingState> preprocessingState = new AtomicReference<>();

  /**
   * Constructor
   *
   * @param baseDir the base folder for process, it must not be null
   */
  public PreprocessorContext(@Nonnull final File baseDir) {
    this.baseDir = assertNotNull("Base folder must not be null", baseDir);
    this.preprocessingState.set(new PreprocessingState(this, this.sourceEncoding, this.targetEncoding));
    setSources(DEFAULT_SOURCE_DIRECTORY).setTarget(new File(DEFAULT_DEST_DIRECTORY));
    registerSpecialVariableProcessor(new JCPSpecialVariableProcessor());
    registerSpecialVariableProcessor(new EnvironmentVariableProcessor());
    this.cloned = false;
    this.currentInCloneSource = null;
  }

  /**
   * Make clone of a preprocessor context but without cloning state.
   *
   * @param context the context to be cloned, must not be null.
   */
  public PreprocessorContext(@Nonnull final PreprocessorContext context) {
    assertNotNull("Source context must not be null", context);

    this.baseDir = context.getBaseDir();
    this.verbose = context.isVerbose();
    this.keepComments = context.isKeepComments();
    this.clearTarget = context.isClearTarget();
    this.dryRun = context.isDryRun();
    this.keepLines = context.isKeepLines();
    this.allowWhitespaces = context.isAllowWhitespaces();
    this.preserveIndents = context.isPreserveIndents();
    this.sources.addAll(context.sources);
    this.target = context.getTarget();
    this.keepAttributes = context.isKeepAttributes();
    this.careForLastEol = context.isCareForLastEol();

    this.extensions.clear();
    this.extensions.addAll(context.extensions);

    this.excludeExtensions.clear();
    this.excludeExtensions.addAll(context.excludeExtensions);

    this.unknownVariableAsFalse = context.unknownVariableAsFalse;

    this.preprocessorExtension = context.getPreprocessorExtension();
    this.sourceEncoding = context.getSourceEncoding();
    this.targetEncoding = context.getTargetEncoding();
    this.dontOverwriteSameContent = context.isDontOverwriteSameContent();
    this.eol = context.getEol();

    this.globalVarTable.clear();
    this.globalVarTable.putAll(context.getGlobalVarTable());

    this.localVarTable.clear();
    this.localVarTable.putAll(context.getLocalVarTable());
    this.excludeFolders = new ArrayList<>(context.getExcludeFolders());

    this.mapVariableNameToSpecialVarProcessor.putAll(context.getMapVariableNameToSpecialVarProcessor());
    this.sharedResources.putAll(context.getSharedResources());

    this.configFiles.clear();
    this.configFiles.addAll(context.getConfigFiles());

    this.preprocessingState.set(assertNotNull(context.getPreprocessingState()));
    this.cloned = true;

    this.preprocessorLogger = context.getPreprocessorLogger();

    this.currentInCloneSource = context.getPreprocessingState().peekFile();
  }

  @Nonnull
  private static String makeStackView(
      @Nullable final TextFileDataContainer cloneSource,
      final boolean cloned,
      @Nullable @MustNotContainNull final List<TextFileDataContainer> list
  ) {
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

  @Nonnull
  private static Charset decodeCharset(@Nonnull final String charsetName) {
    final String normalized = charsetName.trim();
    if (Charset.isSupported(normalized)) {
      return Charset.forName(normalized);
    } else {
      throw new IllegalArgumentException("Unsupported charset: " + charsetName);
    }
  }

  public void setEol(@Nonnull final String eol) {
    this.eol = assertNotNull(eol);
  }

  public void setTarget(@Nonnull final File file) {
    this.target = file.isAbsolute() ? file : new File(this.getBaseDir(), file.getPath());
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
   * Set a shared source, it is an object saved into the inside map for a name
   *
   * @param name the name for the saved project, must not be null
   * @param obj  the object to be saved in, must not be null
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
   * Set source directories
   *
   * @param folderPaths list of source folder paths represented as strings
   * @return this preprocessor context instance
   */
  @Nonnull
  public PreprocessorContext setSources(@Nonnull @MustNotContainNull final List<String> folderPaths) {
    this.sources.clear();
    this.sources.addAll(assertDoesntContainNull(folderPaths).stream().map(x -> new SourceFolder(this.baseDir, x)).collect(Collectors.toList()));
    return this;
  }

  /**
   * Get file extensions of files to be preprocessed as a string array
   *
   * @return a string array of file extensions to be preprocessed
   */
  @Nonnull
  @MustNotContainNull
  public Set<String> getExtensions() {
    return this.extensions;
  }

  /**
   * Set file extensions of files to be preprocessed, it is a comma separated list
   *
   * @param extensions comma separated extensions list of file extensions to be preprocessed, must not be null
   * @return this preprocessor context
   */
  @Nonnull
  public PreprocessorContext setExtensions(@Nonnull @MustNotContainNull final List<String> extensions) {
    this.extensions = new HashSet<>(assertDoesntContainNull(extensions));
    return this;
  }

  /**
   * Check that a file is allowed to be preprocessed fo its extension
   *
   * @param file a file to be checked
   * @return true if the file is allowed, false otherwise
   */
  public final boolean isFileAllowedForPreprocessing(@Nullable final File file) {
    boolean result = false;
    if (file != null && file.isFile() && file.length() != 0L) {
      result = this.extensions.contains(PreprocessorUtils.getFileExtension(file));
    }
    return result;
  }

  /**
   * Check that a file is excluded from preprocessing and coping actions
   *
   * @param file a file to be checked
   * @return true if th file must be excluded, otherwise false
   */
  public final boolean isFileExcludedByExtension(@Nullable final File file) {
    return file == null || !file.isFile() || this.excludeExtensions.contains(PreprocessorUtils.getFileExtension(file));
  }

  /**
   * Get excluded file extension list as a string array
   *
   * @return a string array contains file extensions to be excluded from preprocessing act
   */
  @Nonnull
  public Set<String> getExcludeExtensions() {
    return this.excludeExtensions;
  }

  /**
   * Set comma separated list of file extensions to be excluded from preprocessing
   *
   * @param extensions a comma separated file extension list, it must not be null
   * @return this preprocessor context
   */
  @Nonnull
  public PreprocessorContext setExcludeExtensions(@Nonnull @MustNotContainNull final List<String> extensions) {
    this.excludeExtensions = new HashSet<>(assertDoesntContainNull(extensions));
    return this;
  }

  /**
   * Set a local variable value
   *
   * @param name  the variable name, must not be null, remember that the name will be normalized and will be entirely in lower case
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
   * @param name  the variable name, it must not be null and will be normalized to the supported format
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
   * @param name                    the name for the needed variable, it will be normalized to the supported format
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
      logDebug("Unknown variable '" + name + "' is replaced by FALSE!");
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

    return new File(this.getTarget(), path);
  }

  /**
   * Finds file in source folders, the file can be found only inside source folders and external placement is disabled for security purposes.
   *
   * @param path the file path to find, it must not be null and must be existing file
   * @return detected file object for the path, must not be null
   * @throws IOException if it is impossible to find a file for the path
   */
  @Nonnull
  public File findFileInSources(@Nullable final String path) throws IOException {
    if (path == null) {
      throw makeException("File path is null", null);
    }

    if (path.trim().isEmpty()) {
      throw makeException("File path is empty", null);
    }

    File result = null;

    final TextFileDataContainer theFile = this.getPreprocessingState().peekFile();
    final String parentDir = theFile == null ? null : theFile.getFile().getParent();

    final File resultFile = new File(path);
    if (resultFile.isAbsolute()) {
      // absolute path

      // check that the file is a child of a preprocessing source root else usage of the file is prohibited
      final String normalizedPath = FilenameUtils.normalizeNoEndSeparator(resultFile.getAbsolutePath());
      for (final SourceFolder root : getSources()) {
        if (normalizedPath.startsWith(root.getNormalizedAbsolutePath(true))) {
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
      final List<File> setOfFoundFiles = new ArrayList<>();
      getSources().stream().map((root) -> new File(root.getAsFile(), path)).filter((variant) -> (variant.exists() && variant.isFile())).forEachOrdered((variant) -> {
        setOfFoundFiles.add(variant);
      });

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
  public void registerConfigFile(@Nonnull final File file) {
    assertNotNull("File is null", file);
    this.configFiles.add(file.isAbsolute() ? file : new File(this.getBaseDir(), file.getPath()));
  }

  /**
   * Generate new preprocessing state object, also the new preprocessing state will be saved as the current one in the context
   *
   * @param fileContainer a file container which will be using the preprocessor state, it must not be null
   * @param phaseIndex    index of phase (0 - global, 1 - preprocessing)
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
    this.preprocessingState.set(new PreprocessingState(this, fileContainer, getSourceEncoding(), getTargetEncoding(), this.isDontOverwriteSameContent()));

    return this.getPreprocessingState();
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
    this.preprocessingState.set(new PreprocessingState(this, fileContainer, textContainer, getSourceEncoding(), getTargetEncoding(), this.isDontOverwriteSameContent()));
    return this.getPreprocessingState();
  }

  /**
   * Get the last generated preprocessing state, it is the current one
   *
   * @return the last generated preprocessing state
   */
  @Nonnull
  public PreprocessingState getPreprocessingState() {
    return this.preprocessingState.get();
  }

  /**
   * Prepare exception with message and cause, or return cause if it is a preprocessor exception
   *
   * @param text  the message text, must not be null
   * @param cause the cause, it can be null
   * @return prepared exception with additional information
   */
  @Nonnull
  public PreprocessorException makeException(@Nonnull final String text, @Nullable final Throwable cause) {
    if (cause instanceof PreprocessorException) {
      return (PreprocessorException) cause;
    }

    final FilePositionInfo[] includeStack;
    final String sourceLine;
    includeStack = this.getPreprocessingState().makeIncludeStack();
    sourceLine = this.getPreprocessingState().getLastReadString();
    return new PreprocessorException(text, sourceLine, includeStack, cause);
  }

  public void logForVerbose(@Nonnull final String str) {
    if (isVerbose()) {
      final String stack;
      stack = makeStackView(this.currentInCloneSource, this.cloned, this.getPreprocessingState().getCurrentIncludeStack());
      this.logInfo(str + (stack.isEmpty() ? ' ' : '\n') + stack);
    }
  }

  public final static class SourceFolder {
    private final String path;
    private final File pathFile;

    public SourceFolder(@Nonnull final File baseDir, @Nonnull final String path) {
      this.path = assertNotNull(path);
      final File pathAsFile = new File(path);
      this.pathFile = pathAsFile.isAbsolute() ? pathAsFile : new File(baseDir, path);
    }

    @Nonnull
    public String getAsString() {
      return this.path;
    }

    @Nonnull
    public File getAsFile() {
      return this.pathFile;
    }

    @Nonnull
    public String getNormalizedAbsolutePath(final boolean separatorCharEnded) {
      String result = FilenameUtils.normalizeNoEndSeparator(this.pathFile.getAbsolutePath());
      if (separatorCharEnded) {
        result += File.separatorChar;
      }
      return result;
    }

    @Override
    @Nonnull
    public String toString() {
      return String.format("%s[%s]", this.getClass().getSimpleName(), this.path);
    }
  }
}
