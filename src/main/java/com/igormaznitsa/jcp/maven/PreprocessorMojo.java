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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import com.igormaznitsa.meta.annotation.MustNotContainNull;
import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;
import org.apache.commons.lang3.StringUtils;
import com.igormaznitsa.meta.common.utils.Assertions;

/**
 * The Mojo makes preprocessing of defined or project root source folders and place result in defined or predefined folder, also it can replace the source folder for a maven
 * project to use the preprocessed sources.
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
@Mojo(name = "preprocess", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true, requiresProject = true)
public class PreprocessorMojo extends AbstractMojo implements PreprocessorLogger {

  /**
   * The Project source roots for non-test mode.
   */
  @Parameter(alias = "compileSourceRoots", defaultValue = "${project.compileSourceRoots}", required = true, readonly = true)
  private List<String> compileSourceRoots;

  /**
   * The Project source roots for test mode.
   */
  @Parameter(alias = "testCompileSourceRoots", defaultValue = "${project.testCompileSourceRoots}", required = true, readonly = true)
  private List<String> testCompileSourceRoots;

  /**
   * The Maven Project to be preprocessed.
   */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  /**
   * The Directly defined source directory, it will make plugin to preprocess the folder instead of project and maven defined ones. By default it is empty and is not used.
   */
  @Parameter(alias = "source", defaultValue = "")
  private String source;

  /**
   * Copy file attributes for copied and generated files.
   * @since 6.1.2
   */
  @Parameter(alias = "copyFileAttributes", defaultValue = "false")
  private boolean copyFileAttributes;
  
  /**
   * The Destination folder where generated sources will be placed in non-test mode.
   */
  @Parameter(alias = "destination", defaultValue = "${project.build.directory}/generated-sources/preprocessed")
  private File destination;

  /**
   * Destination folder where generated sources will be placed in test-mode.
   */
  @Parameter(alias = "testDestination", defaultValue = "${project.build.directory}/generated-test-sources/preprocessed")
  private File testDestination;

  /**
   * The Input text encoding to be used for preprocessing, by default it uses defined in project properties.
   */
  @Parameter(alias = "inEncoding", defaultValue = "${project.build.sourceEncoding}")
  private String inEncoding;

  /**
   * The Encoding for preprocessed text output, by default it uses defined in project properties.
   */
  @Parameter(alias = "outEncoding", defaultValue = "${project.build.sourceEncoding}")
  private String outEncoding;

  /**
   * Flag to ignore missing source folders, if false then mojo fail for any missing source folder, if true then missing folder will be ignored.
   * @since 6.1.1
   */
  @Parameter(alias="ignoreMissingSources", defaultValue = "false")
  private boolean ignoreMissingSources;
  
  /**
   * List of file extensions to be excluded from the preprocessing process. By default excluded XML files.
   */
  @Parameter(alias = "excluded")
  private String excluded;

  /**
   * List of file extensions to be preprocessed. By default java,txt,htm,html
   */
  @Parameter(alias = "processing")
  private String processing;

  /**
   * Flag to interpret unknown variable as FALSE.
   */
  @Parameter(alias="unknownVarAsFalse", defaultValue = "false") 
  private boolean unknownVarAsFalse;
  
  /**
   * Make dry run of the preprocessor without any saving of result.
   */
  @Parameter(alias = "disableOut", defaultValue = "false")
  private boolean disableOut;

  /**
   * Turn on the verbose mode for preprocessing process.
   */
  @Parameter(alias = "verbose", defaultValue = "false")
  private boolean verbose;

  /**
   * Clear the destination folder before preprocessing (if it exists).
   */
  @Parameter(alias = "clear", defaultValue = "false")
  private boolean clear;

  /**
   * Be precise in processing of the last next line char in files, it will not be added if it is not presented if to turn on the mode..
   */
  @Parameter(alias = "careForLastNextLine", defaultValue = "false")
  private boolean careForLastNextLine;

  /**
   * Disable overriding of the source root folders for maven project after preprocessing.
   */
  @Parameter(alias = "keepSrcRoot", defaultValue = "false")
  private boolean keepSrcRoot;

  /**
   * Remove all Java like commentaries from preprocessed sources.
   */
  @Parameter(alias = "removeComments", defaultValue = "false")
  private boolean removeComments;

  /**
   * List of global preprocessing variables.
   */
  @Parameter(alias = "globalVars")
  private Properties globalVars;

  /**
   * List of sub-folders in source folders to be excluded from preprocessing, ANT path pattern format allowed.
   */
  @Parameter(alias = "excludedFolders")
  private String [] excludedFolders = new String[0];
  
  /**
   * List of external configuration files.
   */
  @Parameter(alias = "cfgFiles")
  private File[] cfgFiles;

  /**
   * Disable removing lines from preprocessed files, it allows to keep line numeration similar to original sources.
   */
  @Parameter(alias = "keepLines", defaultValue = "true")
  private boolean keepLines;

  /**
   * Manage mode to allow whitespace between the // and the #.
   */
  @Parameter(alias = "allowWhitespace", defaultValue = "false")
  private boolean allowWhitespace;
  
  /**
   * Preserve indents in lines marked by '//$' and '//$$' directives. The Directives will be replaced by white spaces chars.
   */
  @Parameter(alias = "preserveIndent", defaultValue = "false")
  private boolean preserveIndent;
  
  /**
   * Allow usage of the preprocessor for test sources (since 5.3.4 version).
   */
  @Parameter(alias = "useTestSources", defaultValue = "false")
  private boolean useTestSources;

  /**
   * Skip preprocessing.
   * @since 6.1.1
   */
  @Parameter(alias = "skip", property = "jcp.preprocess.skip", defaultValue = "false")
  private boolean skip;
  
  /**
   * Flag to compare generated content with existing file and if it is the same then to not override the file, it brings overhead
   */
  @Parameter(alias = "compareDestination", defaultValue = "false")
  private boolean compareDestination;

  public void setExcludedFolders(@Nonnull @MustNotContainNull final String ... antPatterns) {
    this.excludedFolders = Assertions.assertDoesntContainNull(Assertions.assertNotNull(antPatterns));
  }
  
  @Nonnull
  @MustNotContainNull
  public String [] getExcludedFolders() {
    return this.excludedFolders;
  }
  
  public void setIgnoreMissingSources(final boolean flag) {
    this.ignoreMissingSources = flag;
  }
  
  public boolean isIgnoreMissingSources() {
    return this.ignoreMissingSources;
  }
  
  public void setSkip(final boolean flag) {
    this.skip = flag;
  }
  
  public boolean isSkip() {
    return this.skip;
  }

  public void setPreserveIndent(final boolean flag) {
    this.preserveIndent = flag;
  }
  
  public boolean getPreserveIndent() {
    return this.preserveIndent;
  }
  
  public void setCopyFileAttributes(final boolean flag) {
    this.copyFileAttributes = flag;
  }
  
  public boolean getCopyFileAttributes() {
    return this.copyFileAttributes;
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

  public void setGlobalVars(@Nonnull final Properties vars) {
    this.globalVars = vars;
  }

  @Nonnull
  public Properties getGlobalVars() {
    return this.globalVars;
  }

  public void setCfgFiles(@Nonnull @MustNotContainNull final File[] files) {
    this.cfgFiles = files;
  }

  @Nonnull
  @MustNotContainNull
  public File[] getCfgFiles() {
    return this.cfgFiles;
  }

  public void setCompareDestination(final boolean flag) {
    this.compareDestination = flag;
  }

  public boolean isCompareDestination() {
    return this.compareDestination;
  }

  public void setSource(@Nonnull final String source) {
    this.source = source;
  }

  @Nonnull
  public String getSource() {
    return this.source;
  }

  public void setDestination(@Nonnull final File destination) {
    this.destination = destination;
  }

  @Nonnull
  public File getDestination() {
    return this.destination;
  }

  public void setTestDestination(@Nonnull final File destination) {
    this.testDestination = destination;
  }

  @Nonnull
  public File getTestDestination() {
    return this.testDestination;
  }

  public void setInEncoding(@Nonnull final String value) {
    this.inEncoding = value;
  }

  @Nonnull
  public String getInEncoding() {
    return this.inEncoding;
  }

  public void setOutEncoding(@Nonnull final String value) {
    this.outEncoding = value;
  }

  @Nonnull
  public String getOutEncoding() {
    return this.outEncoding;
  }

  public void setExcluded(@Nullable final String excluded) {
    this.excluded = excluded;
  }

  @Nullable
  public String getExcluded() {
    return this.excluded;
  }

  public void setUnknownVarAsFalse(final boolean flag) {
    this.unknownVarAsFalse = flag;
  }
  
  public boolean getUnknownVarAsFalse() {
    return this.unknownVarAsFalse;
  }
  
  public void setProcessing(@Nullable final String processing) {
    this.processing = processing;
  }

  @Nullable
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

  public void setAllowWhitespace(final boolean flag) {
    this.allowWhitespace = flag;
  }
  
  public boolean getAllowWhitespace() {
    return this.allowWhitespace;
  }
  
  public void setRemoveComments(final boolean value) {
    this.removeComments = value;
  }

  public boolean getRemoveComments() {
    return this.removeComments;
  }

  @Nullable
  private String makeSourceRootList() {
    String result = null;
    if (this.source != null && !this.source.isEmpty()) {
      result = this.source;
    } else if (this.project != null) {
      final StringBuilder accum = new StringBuilder();

      for (final String srcRoot : (this.getUseTestSources() ? this.testCompileSourceRoots : this.compileSourceRoots)) {
        final boolean folderPresented = new File(srcRoot).isDirectory();
        
        if (!folderPresented) {
          getLog().debug("Can't find source folder : "+srcRoot);
        }
        
        String textToAppend;
        
        if (folderPresented) {
          textToAppend = srcRoot;
        } else {
          if (this.isIgnoreMissingSources()) {
            textToAppend = null;
          } else {
            textToAppend = srcRoot;
          }
        }
          
        if (textToAppend!=null) {
        if (accum.length() > 0) {
          accum.append(';');
        }
        accum.append(srcRoot);
        }
      }
      result = accum.toString();
    }
    return result;
  }

  private void replaceSourceRootByPreprocessingDestinationFolder(@Nonnull final PreprocessorContext context) throws IOException {
    if (this.project != null) {
      final String sourceDirectories = context.getSourceDirectories();
      final String[] splitted = sourceDirectories.split(";");

      final List<String> sourceRoots = this.getUseTestSources() ? this.testCompileSourceRoots : this.compileSourceRoots;
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

  @Nonnull
  PreprocessorContext makePreprocessorContext(@Nonnull final String sourceFoldersInPreprocessorFormat) throws IOException {
    final PreprocessorContext context = new PreprocessorContext();
    context.setPreprocessorLogger(this);

    if (this.project != null) {
      final MavenPropertiesImporter mavenPropertiesImporter = new MavenPropertiesImporter(context, project, getVerbose() || getLog().isDebugEnabled());
      context.registerSpecialVariableProcessor(mavenPropertiesImporter);
    }

    context.setSourceDirectories(sourceFoldersInPreprocessorFormat);
    context.setDestinationDirectory(assertNotNull(this.getUseTestSources() ? this.testDestination.getCanonicalPath() : this.destination.getCanonicalPath()));

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

    context.setUnknownVariableAsFalse(this.unknownVarAsFalse);
    context.setCompareDestination(this.compareDestination);
    context.setClearDestinationDirBefore(this.clear);
    context.setCareForLastNextLine(this.careForLastNextLine);
    context.setRemoveComments(this.removeComments);
    context.setVerbose(getLog().isDebugEnabled() || this.verbose);
    context.setKeepLines(this.keepLines);
    context.setFileOutputDisabled(this.disableOut);
    context.setAllowWhitespace(this.allowWhitespace);
    context.setPreserveIndent(this.preserveIndent);
    context.setExcludedFolderPatterns(this.excludedFolders);
    context.setCopyFileAttributes(this.copyFileAttributes);
    
    // process cfg files
    if (this.cfgFiles != null && this.cfgFiles.length != 0) {
      for (final File file : this.cfgFiles) {
        assertNotNull("Detected null where a config file was expected", file);
        context.addConfigFile(file);
      }
    }

    // process global vars
    if (this.globalVars != null && !this.globalVars.isEmpty()) {
      for (final String key : this.globalVars.stringPropertyNames()) {
        final String value = this.globalVars.getProperty(key);
        assertNotNull("Can't find defined value for '" + key + "' global variable", value);
        context.setGlobalVariable(key, Value.recognizeRawString(value));
      }
    }

    return context;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (this.isSkip()) {
      getLog().info("Skip preprocessing");
    } else {
      PreprocessorContext context = null;

      final String sourceFoldersInPreprocessingFormat = makeSourceRootList();

      boolean skipPreprocessing = false;

      if (StringUtils.isEmpty(sourceFoldersInPreprocessingFormat)) {
        if (isIgnoreMissingSources()) {
          getLog().warn("Source folders are not provided, preprocessing is skipped.");
          skipPreprocessing = true;
        } else {
          throw new MojoFailureException("Can't find source folders to preprocess, check parameters and project type!");
        }
      }

      if (!skipPreprocessing) {
        try {
          context = makePreprocessorContext(Assertions.assertNotNull(sourceFoldersInPreprocessingFormat));
        }
        catch (Exception ex) {
          final PreprocessorException pp = PreprocessorException.extractPreprocessorException(ex);
          throw new MojoExecutionException(pp == null ? ex.getMessage() : pp.toString(), pp == null ? ex : pp);
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
          throw new MojoFailureException(pp == null ? ex.getMessage() : PreprocessorException.referenceAsString('.', pp), pp == null ? ex : pp);
        }
      }
    }
  }

  @Override
  public void error(@Nonnull final String message) {
    getLog().error(message);
  }

  @Override
  public void info(@Nonnull final String message) {
    getLog().info(message);
  }

  @Override
  public void warning(@Nonnull final String message) {
    getLog().warn(message);
  }

  @Override
  public void debug(@Nonnull final String message) {
    getLog().debug(message);
  }
}
