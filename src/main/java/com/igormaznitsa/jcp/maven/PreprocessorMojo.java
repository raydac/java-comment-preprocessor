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
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

/**
 * The Mojo makes preprocessing of defined or project root source folders and place result in defined or predefined folder, also it can replace the source folder for a maven project to use the preprocessed sources.
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
@Mojo(name = "preprocess", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true, requiresProject = true)
public class PreprocessorMojo extends AbstractMojo implements PreprocessorLogger {

  /**
   * The Project source roots for non-test mode.
   */
  @Parameter(name="compileSourceRoots", defaultValue = "${project.compileSourceRoots}", required = true, readonly = true)
  private List<String> compileSourceRoots;

  /**
   * The Project source roots for test mode.
   */
  @Parameter(name = "testCompileSourceRoots", defaultValue = "${project.testCompileSourceRoots}", required = true, readonly = true)
  private List<String> testCompileSourceRoots;

  /**
   * The Maven Project to be preprocessed.
   */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  /**
   * The Directly defined source directory, it will make plugin to preprocess the folder instead of project and maven defined ones. By default it is empty and is not used.
   */
  @Parameter(name = "source", defaultValue = "")
  private String source;
  
  /**
   * The Destination folder where generated sources will be placed in non-test mode.
   */
  @Parameter(name = "destination", defaultValue = "${project.build.directory}/generated-sources/preprocessed")
  private File destination;

  /**
   * Destination folder where generated sources will be placed in test-mode.
   */
  @Parameter(name = "testDestination", defaultValue = "${project.build.directory}/generated-test-sources/preprocessed")
  private File testDestination;
  
  /**
   * The Input text encoding to be used for preprocessing, by default it uses defined in project properties.
   */
  @Parameter(name = "inEncoding", defaultValue = "${project.build.sourceEncoding}")
  private String inEncoding;

  /**
   * The Encoding for preprocessed text output, by default it uses defined in
   * project properties.
   */
  @Parameter(name="outEncoding",defaultValue = "${project.build.sourceEncoding}")
  private String outEncoding;
  
  /**
   * List of file extensions to be excluded from the preprocessing process. By default excluded XML files.
   */
  @Parameter(name = "excluded")
  private String excluded;

  /**
   * List of file extensions to be preprocessed. By default java,txt,htm,html
   */
  @Parameter(name = "processing")
  private String processing;

  /**
   * Make dry run of the preprocessor without any saving of result.
   */
  @Parameter(name="disableOut", defaultValue = "false")
  private boolean disableOut;
  
  /**
   * Turn on the verbose mode for preprocessing process.
   */
  @Parameter(name="verbose", defaultValue = "false")
  private boolean verbose;

  /**
   * Clear the destination folder before preprocessing (if it exists).
   */
  @Parameter(name = "clear", defaultValue = "false")
  private boolean clear;
  
  /**
   * Be precise in processing of the last next line char in files, it will not be added if it is not presented if to turn on the mode..
   */
  @Parameter(name = "careForLastNextLine", defaultValue = "false")
  private boolean careForLastNextLine;
  
  /**
   * Disable overriding of the source root folders for maven project after preprocessing.
   */
  @Parameter(name = "keepSrcRoot", defaultValue = "false")
  private boolean keepSrcRoot;

  /**
   * Remove all Java like commentaries from preprocessed sources.
   */
  @Parameter(name = "removeComments", defaultValue = "false")
  private boolean removeComments;
  
  /**
   * List of global preprocessing variables.
   */
  @Parameter(name = "globalVars")
  private Properties globalVars;

  /**
   * List of external configuration files.
   */
  @Parameter(name = "cfgFiles")
  private File[] cfgFiles;

  /**
   * Disable removing lines from preprocessed files, it allows to keep line numeration similar to original sources.
   */
  @Parameter(name = "keepLines", defaultValue = "true")
  private boolean keepLines;

  /**
   * Allow usage of the preprocessor for test sources (since 5.3.4 version).
   */
  @Parameter(name="useTestSources", defaultValue = "false")
  private boolean useTestSources;

  public PreprocessorMojo() {
    super();
  }

  public void setUseTestSources(final boolean flag) {
    this.useTestSources = flag;
  }

  public boolean getUseTestSources() {
    return this.useTestSources;
  }

  public void setClear(final boolean flag) {
    this.clear = flag;
  }

  public boolean getClear() {
    return this.clear;
  }

  public void setCareForLastNextLine(final boolean flag) {
    this.careForLastNextLine = flag;
  }

  public boolean getCarForLastNextLine() {
    return this.careForLastNextLine;
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

  public void setTestDestination(final File destination) {
    this.testDestination = destination;
  }

  public File getTestDestination() {
    return this.testDestination;
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
    if (this.source != null && !this.source.isEmpty()) {
      result = this.source;
    }
    else if (this.project != null) {
      final StringBuilder accum = new StringBuilder();

      for (final String srcRoot : (this.useTestSources ? this.testCompileSourceRoots : this.compileSourceRoots)) {
        if (accum.length() > 0) {
          accum.append(';');
        }
        accum.append(srcRoot);
      }
      result = accum.toString();
    }
    return result;
  }

  private void replaceSourceRootByPreprocessingDestinationFolder(final PreprocessorContext context) throws IOException {
    if (this.project != null) {
      final String sourceDirectories = context.getSourceDirectories();
      final String[] splitted = sourceDirectories.split(";");

      final List<String> sourceRoots = this.useTestSources ? this.testCompileSourceRoots : this.compileSourceRoots;
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

    if (this.project != null) {
      final MavenPropertiesImporter mavenPropertiesImporter = new MavenPropertiesImporter(context, project);
      context.registerSpecialVariableProcessor(mavenPropertiesImporter);
    }

    context.setSourceDirectories(makeSourceRootList());
    context.setDestinationDirectory(this.useTestSources ? this.testDestination.getCanonicalPath() : this.destination.getCanonicalPath());

    if (this.inEncoding != null) {
      context.setInCharacterEncoding(this.inEncoding);
    }
    if (this.outEncoding != null) {
      context.setOutCharacterEncoding(this.outEncoding);
    }
    if (this.excluded != null) {
      context.setExcludedFileExtensions(this.excluded);
    }
    if (this.processing != null) {
      context.setProcessingFileExtensions(this.processing);
    }

    info("Preprocess sources : " + context.getSourceDirectories());
    info("Preprocess destination : " + context.getDestinationDirectory());

    context.setClearDestinationDirBefore(this.clear);
    context.setCareForLastNextLine(this.careForLastNextLine);
    context.setRemoveComments(this.removeComments);
    context.setVerbose(this.verbose);
    context.setKeepLines(this.keepLines);
    context.setFileOutputDisabled(this.disableOut);

    // process cfg files
    if (this.cfgFiles != null && this.cfgFiles.length != 0) {
      for (final File file : this.cfgFiles) {
        PreprocessorUtils.assertNotNull("Detected null where a config file was expected", file);
        context.addConfigFile(file);
      }
    }

    // process global vars
    if (this.globalVars != null && !this.globalVars.isEmpty()) {
      for (final String key : this.globalVars.stringPropertyNames()) {
        final String value = this.globalVars.getProperty(key);
        PreprocessorUtils.assertNotNull("Can't find defined value for '" + key + "' global variable",value);
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
      final PreprocessorException pp = PreprocessorException.extractPreprocessorException(ex);
      throw new MojoExecutionException(pp==null ? ex.getMessage() : pp.toString(), pp == null ? ex : pp);
    }

    try {
      final JCPreprocessor preprocessor = new JCPreprocessor(context);
      preprocessor.execute();
      if (!getKeepSrcRoot()) {
        replaceSourceRootByPreprocessingDestinationFolder(context);
      }
    }
    catch (Exception ex) {
      final PreprocessorException pp = PreprocessorException.extractPreprocessorException(ex);
      throw new MojoFailureException(pp == null ? ex.getMessage() : PreprocessorException.referenceAsString('.',pp), pp == null ? ex : pp);
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
