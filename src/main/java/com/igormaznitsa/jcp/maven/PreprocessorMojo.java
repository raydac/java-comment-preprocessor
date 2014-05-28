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
package com.igormaznitsa.jcp.maven;

import com.igormaznitsa.jcp.JCPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * It allow to make preprocessing of sources and text data in maven projects.
 *
 * @goal preprocess
 * @phase generate-sources
 * @threadSafe
 * @requiresProject
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class PreprocessorMojo extends AbstractMojo implements PreprocessorLogger {

  /**
   * The project to be preprocessed.
   *
   * @parameter default-value="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;
  /**
   * Source directory
   *
   * @parameter name="source"
   * @readonly
   */
  private String source;
  /**
   * Destination directory
   *
   * @parameter name="destination" default-value="${project.build.directory}/generated-sources/preprocessed"
   * @readonly
   */
  private File destination;
  /**
   * Input text character encoding
   *
   * @parameter name="inEncoding" default-value="${project.build.sourceEncoding}"
   * @readonly
   */
  private String inEncoding;
  /**
   * Output text character encoding
   *
   * @parameter name="outEncoding" default-value="${project.build.sourceEncoding}"
   * @readonly
   */
  private String outEncoding;
  /**
   * List of excluded extensions from preprocessing
   *
   * @parameter name="excluded" 
   * @readonly
   */
  private String excluded;
  /**
   * List of extensions to be preprocessed
   *
   * @parameter name="processing"
   * @readonly
   */
  private String processing;
  /**
   * Disable any output file operations
   *
   * @parameter name="disableOut" default-value="false"
   * @readonly
   */
  private boolean disableOut;
  /**
   * Make verbose message output for inside processes
   *
   * @parameter name="verbose" default-value="false"
   * @readonly
   */
  private boolean verbose;
  /**
   * Clear the destination directory before preprocessing if it is existing
   *
   * @parameter name="clear" default-value="false"
   * @readonly
   */
  private boolean clear;
  /**
   * Flag to disable overriding of the source root directory by preprocessed
   * directory
   *
   * @parameter name="keepSrcRoot" default-value="false"
   */
  private boolean keepSrcRoot;
  /**
   * Remove all Java like commentaries from the resulting files
   *
   * @parameter name="removeComments" default-value="false"
   * @readonly
   */
  private boolean removeComments;
  /**
   * Global variables
   *
   * @parameter name="globalVars"
   * @readonly
   */
  private Properties globalVars;
  /**
   * Configuration files
   *
   * @parameter name="cfgFiles"
   * @readonly
   */
  private File[] cfgFiles;
  /**
   * Do not remove non-processing strings from the output
   *
   * @parameter name="keepLines" default-value="false"
   * @readonly
   */
  private boolean keepLines;

  public PreprocessorMojo() {
    super();
  }

  public void setClear(final boolean flag) {
    this.clear = flag;
  }

  public boolean getClear() {
    return this.clear;
  }

  public void setKeepSrcRoot(final boolean flag) {
    this.keepSrcRoot = flag;
  }

  public boolean getKeepSrcRoot() {
    return this.keepSrcRoot;
  }

  public void setGlobalVars(final Properties vars) {
    this.globalVars = vars;
  }

  public Properties getGlobalVars() {
    return this.globalVars;
  }

  public void setCfgFiles(final File[] files) {
    this.cfgFiles = files;
  }

  public File[] getCfgFiles() {
    return this.cfgFiles;
  }

  public void setSource(final String source) {
    this.source = source;
  }

  public String getSource() {
    return this.source;
  }

  public void setDestination(final File destination) {
    this.destination = destination;
  }

  public File getDestination() {
    return this.destination;
  }

  public void setInEncoding(final String value) {
    this.inEncoding = value;
  }

  public String getInEncoding() {
    return this.inEncoding;
  }

  public void setOutEncoding(final String value) {
    this.outEncoding = value;
  }

  public String getOutEncoding() {
    return this.outEncoding;
  }

  public void setExcluded(final String excluded) {
    this.excluded = excluded;
  }

  public String getExcluded() {
    return this.excluded;
  }

  public void setProcessing(final String processing) {
    this.processing = processing;
  }

  public String getProcessing() {
    return this.processing;
  }

  public void setDisableOut(final boolean value) {
    this.disableOut = value;
  }

  public boolean getDisableOut() {
    return this.disableOut;
  }

  public void setVerbose(final boolean verbose) {
    this.verbose = verbose;
  }

  public boolean getVerbose() {
    return this.verbose;
  }

  public void setKeepLines(final boolean keepLines) {
    this.keepLines = keepLines;
  }

  public boolean getKeepLines() {
    return this.keepLines;
  }

  public void setRemoveComments(final boolean value) {
    this.removeComments = value;
  }

  public boolean getRemoveComments() {
    return this.removeComments;
  }

  private String makeSourceRootList() {
    String result = null;
    if (source != null) {
      result = source;
    }
    else if (project != null) {
      final StringBuilder accum = new StringBuilder();
      for (final String srcRoot : project.getCompileSourceRoots()) {
        if (accum.length() > 0) {
          accum.append(';');
        }
        accum.append(srcRoot);
      }
      result = accum.toString();
    }
    return result;
  }

  private void addPreprocessedAsSourceRoot(final PreprocessorContext context) throws IOException {
    if (project != null) {
      final String sourceDirectories = context.getSourceDirectories();
      final String[] splitted = sourceDirectories.split(";");

      final List<String> sourceRoots = project.getCompileSourceRoots();
      final List<String> sourceRootsAsCanonical = new ArrayList<String>();
      for (final String src : sourceRoots) {
        sourceRootsAsCanonical.add(new File(src).getCanonicalPath());
      }

      for (final String str : splitted) {
        int index = sourceRoots.indexOf(str);
        if (index < 0) {
          // check for canonical paths
          final File src = new File(str);
          final String canonicalPath = src.getCanonicalPath();
          index = sourceRootsAsCanonical.indexOf(canonicalPath);
        }
        if (index >= 0) {
          info("A Compile source root has been removed from the root list [" + sourceRoots.get(index) + ']');
          sourceRoots.remove(index);
        }
      }

      final String destinationDir = context.getDestinationDirectoryAsFile().getCanonicalPath();

      sourceRoots.add(destinationDir);
      info("The New compile source root has been added into the list [" + destinationDir + ']');
    }
  }

  PreprocessorContext makePreprocessorContext() throws IOException {
    final PreprocessorContext context = new PreprocessorContext();
    context.setPreprocessorLogger(this);

    if (project != null) {
      final MavenPropertiesImporter mavenPropertiesImporter = new MavenPropertiesImporter(context, project);
      context.registerSpecialVariableProcessor(mavenPropertiesImporter);
    }

    context.setSourceDirectories(makeSourceRootList());
    context.setDestinationDirectory(destination.getCanonicalPath());

    if (inEncoding != null) {
      context.setInCharacterEncoding(inEncoding);
    }
    if (outEncoding != null) {
      context.setOutCharacterEncoding(outEncoding);
    }
    if (excluded != null) {
      context.setExcludedFileExtensions(excluded);
    }
    if (processing != null) {
      context.setProcessingFileExtensions(processing);
    }

    info("Preprocessing sources folder : " + context.getSourceDirectories());
    info("Preprocessing destination folder : " + context.getDestinationDirectory());

    context.setClearDestinationDirBefore(clear);
    context.setRemoveComments(removeComments);
    context.setVerbose(verbose);
    context.setKeepLines(keepLines);
    context.setFileOutputDisabled(disableOut);

    // process cfg files
    if (cfgFiles != null && cfgFiles.length != 0) {
      for (final File file : cfgFiles) {
        if (file == null) {
          throw new NullPointerException("A NULL in place of a config file detected");
        }

        context.addConfigFile(file);
      }
    }

    // process global vars
    if (globalVars != null && !globalVars.isEmpty()) {
      for (final String key : globalVars.stringPropertyNames()) {
        final String value = globalVars.getProperty(key);
        if (value == null) {
          throw new NullPointerException("Can't find defined value for '" + key + "' global variable");
        }
        context.setGlobalVariable(key, Value.recognizeRawString(value));
      }
    }

    return context;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    PreprocessorContext context = null;

    try {
      context = makePreprocessorContext();
    }
    catch (Exception ex) {
      throw new MojoExecutionException("Exception during preprocessor context creation", ex);
    }

    try {
      final JCPreprocessor preprocessor = new JCPreprocessor(context);
      preprocessor.execute();
      if (!getKeepSrcRoot()) {
        addPreprocessedAsSourceRoot(context);
      }
    }
    catch (Exception ex) {
      throw new MojoFailureException("Exception during preprocessing or preparation", ex);
    }

  }

  @Override
  public void error(final String message) {
    getLog().error(message);
  }

  @Override
  public void info(String message) {
    getLog().info(message);
  }

  @Override
  public void warning(String message) {
    getLog().warn(message);
  }
}
