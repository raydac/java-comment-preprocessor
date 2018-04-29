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
package com.igormaznitsa.jcp.ant;

import com.igormaznitsa.jcp.JCPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.context.SpecialVariableProcessor;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.igormaznitsa.meta.annotation.ImplementationNote;
import com.igormaznitsa.meta.annotation.MustNotContainNull;
import static com.igormaznitsa.meta.common.utils.Assertions.*;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;

/**
 * The class implements an ANT task to allow calls for preprocessing from ANT build scripts. Also it allows to out messages from preprocessor directives into the ANT log and read
 * ANT properties as global variables (with the "ant." prefix)
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class PreprocessTask extends Task implements PreprocessorLogger, SpecialVariableProcessor {

  private File sourceDirectory = null;
  private File destinationDirectory = null;

  private String inCharSet = null;
  private String outCharSet = null;
  private String excludedExtensions = null;
  private String processing = null;
  private String excludedFolders = null;
  private boolean disableOut = false;
  private boolean verbose = false;
  private boolean clearDstFlag = false;
  private boolean removeComments = false;
  private boolean keepLines = false;
  private boolean careForLastNextLine = false;
  private boolean compareDestination = false;
  private boolean allowWhitespace = false;
  private boolean preserveIndent = false;
  private boolean copyFileAttributes = false;
  private boolean unknownVarAsFalse = false;
  
  private Map<String, Value> antVariables;
  private final List<Global> globalVariables = new ArrayList<Global>();
  private final List<CfgFile> configFiles = new ArrayList<CfgFile>();

  /**
   * Inside class describes a "cfgfile" item, it has the only attribute "file", the attribute must be defined
   *
   * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
   */
  @ImplementationNote("It is mutable and with default constructor for calls from ANT")
  public static class CfgFile {

    private File file;

    public void setFile(@Nonnull final File file) {
      this.file = assertNotNull(file);
    }

    @Nullable
    public File getFile() {
      return this.file;
    }
  }

  /**
   * Inside class describes a "global" item, it describes a global variable which will be added into the preprocessor context It has attributes "name" and "value", be careful in
   * the value attribute usage because you have to use "&quot;" instead of \" symbol inside string values
   *
   * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
   */
  @ImplementationNote("It is mutable and with default constructor for calls from ANT")
  public static class Global {

    private String name;
    private String value;

    public void setName(@Nonnull final String name) {
      this.name = assertNotNull(name);
    }

    @Nullable
    public String getName() {
      return this.name;
    }

    public void setValue(@Nonnull final String value) {
      this.value = assertNotNull(value);
    }

    @Nullable
    public String getValue() {
      return this.value;
    }
  }

  /**
   * Set the "copyfileattributes", it turns on mode to copy file attributes if file generated or copied.
   * @param flag true if to copy attributes, false otherwise
   */
  public void setCopyFileAttributes(final boolean flag) {
    this.copyFileAttributes = flag;
  }
  
  /**
   * Set the "allowWhitespace", it allows to manage the mode to allow whitespace between the // and the #.
   * @param flag true if whitespace is allowed, false otherwise
   */
  public void setAllowWhitespace(final boolean flag) {
    this.allowWhitespace = flag;
  }
  
  /**
   * Set the "preserveident" attribute, to preserve spaces occupied by '//$' and '//$$' directives.
   * @param flag true to preserve positions of tail chars in lines marked by '//$$' and '//$', false otherwise
   */
  public void setPreserveIndent(final boolean flag) {
    this.preserveIndent = flag;
  }
  
  /**
   * Set the "compareDestination" attribute, it allows to turn on the mode to compare destination file content and to not override the file by generated one if there is the same
   * content.
   *
   * @param flag true if to compare destination file content, false otherwise
   */
  public void setCompareDestiation(final boolean flag) {
    this.compareDestination = flag;
  }

  /**
   * Set the "source" attribute, it allows to define the source directory to be preprocessed
   *
   * @param src a directory to be used as the source one, must not be null
   */
  public void setSource(@Nonnull final File src) {
    this.sourceDirectory = src;
  }

  /**
   * Set the "careforlastnextline" attribute, it allows to make precise processing of last next line char
   *
   * @param flag shows to turn on or turn off the mode
   */
  public void setCareForLastNextLine(final boolean flag) {
    this.careForLastNextLine = flag;
  }

  /**
   * Set the "destination" attribute, it allows to define the destination directory where the preprocessed files will be placed in
   *
   * @param dst a directory to be used as the destination one, must not be null
   */
  public void setDestination(@Nonnull final File dst) {
    this.destinationDirectory = dst;
  }

  /**
   * Set the "inCharset" attribute, it allows to define the text encoding for the reading text files
   *
   * @param charSet the character set to be used to decode read texts, must not be null
   */
  public void setInCharset(@Nonnull final String charSet) {
    this.inCharSet = charSet;
  }

  /**
   * Set the "outCharset" attribute, it allows to define the text encoding for the writing text files
   *
   * @param charSet the character set to be used to encode written texts, must not be null
   */
  public void setOutCharset(@Nonnull final String charSet) {
    this.outCharSet = charSet;
  }

  /**
   * Set the "unknownVarAsFalse" attribute, it allows to interpret unknown variables as FALSE.
   * @param flag true to turn on the mode, false otherwise.
   */
  public void setUnknownVarAsFalse(final boolean flag) {
    this.unknownVarAsFalse = flag;
  }
  
  /**
   * Set the "excluded" attribute, it defines the excluded file extensions which will be ignored by the preprocessor in its work (also those files will not be copied)
   *
   * @param ext the list of ignored file extensions, must not be null
   */
  public void setExcluded(@Nonnull final String ext) {
    this.excludedExtensions = ext;
  }

  /**
   * Set the "processing" attribute, it defines the file extensions to be processed
   *
   * @param ext the list of file extensions which should be preprocessed, must not be null
   */
  public void setProcessing(@Nonnull final String ext) {
    this.processing = ext;
  }

  /**
   * Set the "excludedfolders" attribute, sub-folders in source folders to be excluded from preprocessing, ANT patterns allowed, ${path.separator} should be used for multiple items
   * @param value folder names as string
   */
  public void setExcludedFolders(@Nonnull final String value) {
    this.excludedFolders = value;
  }
  
  /**
   * Set the "clear" attribute, it is a boolean attribute allows to make the preprocessor to clear the destination directory before its work
   *
   * @param flag true if the destination directory must be cleared before preprocessing, otherwise false
   */
  public void setClear(final boolean flag) {
    this.clearDstFlag = flag;
  }

  /**
   * Set the "removeComments" attribute, it is a boolean attribute allows to make the preprocessor to remove all Java-like comments from the result files
   *
   * @param flag true if the result file must be cleared from comments, otherwise false
   */
  public void setRemoveComments(final boolean flag) {
    this.removeComments = flag;
  }

  /**
   * Set the "verbose" attribute, it is a boolean attribute allows to set the verbose level of preprocessor messages
   *
   * @param flag true if the verbose level must be set, otherwise false
   */
  public void setVerbose(final boolean flag) {
    this.verbose = flag;
  }

  /**
   * Set the "keepLines" attribute, it is a boolean attribute to keep non-executing lines as commented ones in the output
   *
   * @param flag true if preprocessor should keep the lines as commented ones, false otherwise
   */
  public void setKeepLines(final boolean flag) {
    this.keepLines = flag;
  }

  /**
   * Set the "disableOut" attribute, it is a boolean attribute allows to disable any output operations into the destination directory
   *
   * @param flag true if the output operations must be disabled, otherwise false
   */
  public void setDisableOut(final boolean flag) {
    this.disableOut = flag;
  }

  @Nonnull
  @ImplementationNote("Do not change because for ANT!")
  public Global createGlobal() {
    final Global result = new Global();
    globalVariables.add(result);
    return result;
  }

  @Nonnull
  @ImplementationNote("Do not change because for ANT!")
  public CfgFile createCfgFile() {
    final CfgFile result = new CfgFile();
    configFiles.add(result);
    return result;
  }

  private void fillCfgFiles(@Nonnull final PreprocessorContext context) {
    for (final CfgFile f : configFiles) {
      context.addConfigFile(assertNotNull("File must not be null", f.getFile()));
    }
  }

  private void fillGlobalVars(@Nonnull final PreprocessorContext context) {
    for (final Global g : globalVariables) {
      context.setGlobalVariable(assertNotNull("Name must not be null", g.getName()), Value.recognizeRawString(assertNotNull("Value must not be null", g.getValue())));
    }
  }

  @Nonnull
  PreprocessorContext generatePreprocessorContext() {
    fillAntVariables();

    final PreprocessorContext context = new PreprocessorContext();
    context.setPreprocessorLogger(this);
    context.registerSpecialVariableProcessor(this);

    if (this.destinationDirectory != null) {
      context.setDestinationDirectory(this.destinationDirectory.getAbsolutePath());
    }

    if (this.sourceDirectory != null) {
      context.setSourceDirectories(this.sourceDirectory.getAbsolutePath());
    } else {
      context.setSourceDirectories(getProject().getBaseDir().getAbsolutePath());
    }

    if (this.excludedExtensions != null) {
      context.setExcludedFileExtensions(this.excludedExtensions);
    }

    if (this.processing != null) {
      context.setProcessingFileExtensions(this.processing);
    }

    if (this.inCharSet != null) {
      context.setInCharacterEncoding(this.inCharSet);
    }

    if (outCharSet != null) {
      context.setOutCharacterEncoding(this.outCharSet);
    }

    context.setCompareDestination(this.compareDestination);
    context.setClearDestinationDirBefore(this.clearDstFlag);
    context.setFileOutputDisabled(this.disableOut);
    context.setRemoveComments(this.removeComments);
    context.setVerbose(this.verbose);
    context.setKeepLines(this.keepLines);
    context.setCareForLastNextLine(this.careForLastNextLine);
    context.setAllowWhitespace(this.allowWhitespace);
    context.setPreserveIndent(this.preserveIndent);
    context.setCopyFileAttributes(this.copyFileAttributes);
    context.setUnknownVariableAsFalse(this.unknownVarAsFalse);
    
    if (this.excludedFolders!=null && !this.excludedFolders.isEmpty()) {
      context.setExcludedFolderPatterns(PreprocessorUtils.splitForChar(this.excludedFolders,File.pathSeparatorChar));
    }
    
    fillCfgFiles(context);
    fillGlobalVars(context);

    return context;
  }

  @Override
  public void execute() throws BuildException {
    PreprocessorContext context = null;
    JCPreprocessor preprocessor;

    try {
      context = generatePreprocessorContext();
    } catch (Exception unexpected) {
      final PreprocessorException pp = PreprocessorException.extractPreprocessorException(unexpected);
      throw new BuildException(pp == null ? unexpected.getMessage() : pp.toString(), pp == null ? unexpected : pp);
    }

    preprocessor = new JCPreprocessor(context);

    try {
      preprocessor.execute();
    } catch (Exception unexpected) {
      final PreprocessorException pp = PreprocessorException.extractPreprocessorException(unexpected);
      throw new BuildException(pp == null ? unexpected.getMessage() : pp.toString(), pp == null ? unexpected : pp);
    }
  }

  @Override
  public void error(@Nullable final String message) {
    log(message, Project.MSG_ERR);
  }

  @Override
  public void info(@Nullable final String message) {
    log(message, Project.MSG_INFO);
  }

  @Override
  public void debug(@Nullable final String message) {
    log(message, Project.MSG_DEBUG);
  }

  @Override
  public void warning(@Nullable final String message) {
    log(message, Project.MSG_WARN);
  }

  private void fillAntVariables() {
    final Project theProject = getProject();

    Map<String, Value> result;

    if (theProject == null) {
      result = Collections.emptyMap();
    } else {

      result = new HashMap<String, Value>();

      for (final Object key : getProject().getProperties().keySet()) {
        final String keyStr = key.toString();
        final String value = theProject.getProperty(keyStr);
        if (value != null) {
          result.put("ant." + keyStr.toLowerCase(Locale.ENGLISH), Value.valueOf(value));
        }
      }
    }
    antVariables = result;
  }

  @Override
  @Nonnull
  @MustNotContainNull
  public String[] getVariableNames() {
    String[] result;

    if (antVariables == null) {
      result = new String[0];
    } else {
      result = antVariables.keySet().toArray(new String[antVariables.size()]);
    }

    return result;
  }

  @Override
  @Nonnull
  public Value getVariable(@Nonnull final String varName, @Nonnull final PreprocessorContext context) {
    if (antVariables == null) {
      throw context.makeException("Non-initialized ANT property map detected", null);
    }
    final Value result = antVariables.get(varName);

    if (result == null) {
      throw context.makeException("Request for unsupported Ant property \'" + varName + '\'', null);
    }
    return result;
  }

  @Override
  public void setVariable(@Nonnull final String varName, @Nonnull final Value value, @Nonnull final PreprocessorContext context) {
    throw context.makeException("Request to change ANT property \'" + varName + "\'. NB! ANT properties are read only!", null);
  }
}
