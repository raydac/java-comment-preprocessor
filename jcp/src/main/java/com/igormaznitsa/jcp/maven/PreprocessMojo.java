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

package com.igormaznitsa.jcp.maven;

import com.igormaznitsa.jcp.JCPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import com.igormaznitsa.meta.annotation.MustNotContainNull;
import com.igormaznitsa.meta.common.utils.Assertions;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

/**
 * The Mojo makes preprocessing of defined or project root source folders and place result in defined or predefined folder, also it can replace the source folder for a maven
 * project to use the preprocessed sources.
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
@Mojo(name = "preprocess", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true, requiresProject = true)
public class PreprocessMojo extends AbstractMojo implements PreprocessorLogger {

  /**
   * The Project source roots for non-test mode.
   */
  @Parameter(alias = "compileSourceRoots", defaultValue = "${project.compileSourceRoots}", required = true, readonly = true)
  private List<String> compileSourceRoots = new ArrayList<>();

  /**
   * The Project source roots for test mode.
   */
  @Parameter(alias = "testCompileSourceRoots", defaultValue = "${project.testCompileSourceRoots}", required = true, readonly = true)
  private List<String> testCompileSourceRoots = new ArrayList<>();

  /**
   * The Maven Project to be preprocessed.
   */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  /**
   * The Directly defined source directory, it will make plugin to preprocess the folder instead of project and maven defined ones. By default it is empty and is not used.
   */
  @Parameter(alias = "source", defaultValue = "")
  private List<String> source = new ArrayList<>();

  /**
   * Copy file attributes for copied and generated files.
   *
   * @since 6.1.2
   */
  @Parameter(alias = "copyFileAttributes", defaultValue = "false")
  private boolean copyFileAttributes = false;

  /**
   * The Destination folder where generated sources will be placed in non-test mode.
   */
  @Parameter(alias = "destination", defaultValue = "${project.build.directory}/generated-sources/preprocessed")
  private File destination = null;

  /**
   * Destination folder where generated sources will be placed in test-mode.
   */
  @Parameter(alias = "testDestination", defaultValue = "${project.build.directory}/generated-test-sources/preprocessed")
  private File testDestination = null;

  /**
   * The Input text encoding to be used for preprocessing, by default it uses defined in project properties.
   */
  @Parameter(alias = "inEncoding", defaultValue = "${project.build.sourceEncoding}")
  private String inEncoding = StandardCharsets.UTF_8.name();

  /**
   * The Encoding for preprocessed text output, by default it uses defined in project properties.
   */
  @Parameter(alias = "outEncoding", defaultValue = "${project.build.sourceEncoding}")
  private String outEncoding = StandardCharsets.UTF_8.name();

  /**
   * Flag to ignore missing source folders, if false then mojo fail for any missing source folder, if true then missing folder will be ignored.
   *
   * @since 6.1.1
   */
  @Parameter(alias = "ignoreMissingSources", defaultValue = "false")
  private boolean ignoreMissingSources = false;

  /**
   * List of file extensions to be excluded from the preprocessing process. By default excluded XML files.
   */
  @Parameter(alias = "excluded")
  private List<String> excluded = new ArrayList<>();

  /**
   * List of file extensions to be preprocessed. By default java,txt,htm,html
   */
  @Parameter(alias = "processing")
  private List<String> processing = new ArrayList<>();

  /**
   * Flag to interpret unknown variable as FALSE.
   */
  @Parameter(alias = "unknownVarAsFalse", defaultValue = "false")
  private boolean unknownVarAsFalse = false;

  /**
   * Make dry run of the preprocessor without any saving of result.
   */
  @Parameter(alias = "disableOut", defaultValue = "false")
  private boolean disableOut = false;

  /**
   * Turn on the verbose mode for preprocessing process.
   */
  @Parameter(alias = "verbose", defaultValue = "false")
  private boolean verbose = false;

  /**
   * Clear the destination folder before preprocessing (if it exists).
   */
  @Parameter(alias = "clear", defaultValue = "false")
  private boolean clear = false;

  /**
   * Be precise in processing of the last next line char in files, it will not be added if it is not presented if to turn on the mode..
   */
  @Parameter(alias = "careForLastNextLine", defaultValue = "false")
  private boolean careForLastNextLine = false;

  /**
   * Disable overriding of the source root folders for maven project after preprocessing.
   */
  @Parameter(alias = "keepSrcRoot", defaultValue = "false")
  private boolean keepSrcRoot = false;

  /**
   * Remove all Java like commentaries from preprocessed sources.
   */
  @Parameter(alias = "removeComments", defaultValue = "false")
  private boolean removeComments = false;

  /**
   * List of global preprocessing variables.
   */
  @Parameter(alias = "globalVars")
  private Map<String, String> globalVars = new HashMap<>();

  /**
   * List of sub-folders in source folders to be excluded from preprocessing, ANT path pattern format allowed.
   */
  @Parameter(alias = "excludedFolders")
  private List<String> excludedFolders = new ArrayList<>();

  /**
   * List of external configuration files.
   */
  @Parameter(alias = "cfgFiles")
  private List<File> cfgFiles = new ArrayList<>();

  /**
   * Disable removing lines from preprocessed files, it allows to keep line numeration similar to original sources.
   */
  @Parameter(alias = "keepLines", defaultValue = "true")
  private boolean keepLines = true;

  /**
   * Manage mode to allow whitespace between the // and the #.
   */
  @Parameter(alias = "allowWhitespace", defaultValue = "false")
  private boolean allowWhitespace = false;

  /**
   * Preserve indents in lines marked by '//$' and '//$$' directives. The Directives will be replaced by white spaces chars.
   */
  @Parameter(alias = "preserveIndent", defaultValue = "false")
  private boolean preserveIndent = false;

  /**
   * Allow usage of the preprocessor for test sources (since 5.3.4 version).
   */
  @Parameter(alias = "useTestSources", defaultValue = "false")
  private boolean useTestSources = false;

  /**
   * Skip preprocessing.
   *
   * @since 6.1.1
   */
  @Parameter(alias = "skip", property = "jcp.preprocess.skip", defaultValue = "false")
  private boolean skip = false;

  /**
   * Flag to compare generated content with existing file and if it is the same then to not override the file, it brings overhead.
   */
  @Parameter(alias = "compareDestination", defaultValue = "false")
  private boolean compareDestination = false;

  @Nonnull
  @MustNotContainNull
  public List<String> getExcludedFolders() {
    return this.excludedFolders;
  }

  public void setExcludedFolders(@Nonnull @MustNotContainNull final String... antPatterns) {
    this.excludedFolders = Arrays.asList(Assertions.assertDoesntContainNull(Assertions.assertNotNull(antPatterns)));
  }

  public boolean isIgnoreMissingSources() {
    return this.ignoreMissingSources;
  }

  public void setIgnoreMissingSources(final boolean flag) {
    this.ignoreMissingSources = flag;
  }

  public boolean isSkip() {
    return this.skip;
  }

  public void setSkip(final boolean flag) {
    this.skip = flag;
  }

  public boolean getPreserveIndent() {
    return this.preserveIndent;
  }

  public void setPreserveIndent(final boolean flag) {
    this.preserveIndent = flag;
  }

  public boolean getCopyFileAttributes() {
    return this.copyFileAttributes;
  }

  public void setCopyFileAttributes(final boolean flag) {
    this.copyFileAttributes = flag;
  }

  public boolean getUseTestSources() {
    return this.useTestSources;
  }

  public void setUseTestSources(final boolean flag) {
    this.useTestSources = flag;
  }

  public boolean getClear() {
    return this.clear;
  }

  public void setClear(final boolean flag) {
    this.clear = flag;
  }

  public void setCareForLastNextLine(final boolean flag) {
    this.careForLastNextLine = flag;
  }

  public boolean getCarForLastNextLine() {
    return this.careForLastNextLine;
  }

  public boolean getKeepSrcRoot() {
    return this.keepSrcRoot;
  }

  public void setKeepSrcRoot(final boolean flag) {
    this.keepSrcRoot = flag;
  }

  @Nonnull
  public Map<String, String> getGlobalVars() {
    return this.globalVars;
  }

  public void setGlobalVars(@Nonnull final Map<String, String> vars) {
    this.globalVars = vars;
  }

  @Nonnull
  @MustNotContainNull
  public List<File> getCfgFiles() {
    return this.cfgFiles;
  }

  public void setCfgFiles(@Nonnull @MustNotContainNull final List<File> files) {
    this.cfgFiles = files;
  }

  public boolean isCompareDestination() {
    return this.compareDestination;
  }

  public void setCompareDestination(final boolean flag) {
    this.compareDestination = flag;
  }

  @Nonnull
  @MustNotContainNull
  public List<String> getSource() {
    return this.source;
  }

  public void setSource(@Nonnull @MustNotContainNull final List<String> source) {
    this.source = source;
  }

  @Nonnull
  public File getDestination() {
    return this.destination;
  }

  public void setDestination(@Nonnull final File destination) {
    this.destination = destination;
  }

  @Nonnull
  public File getTestDestination() {
    return this.testDestination;
  }

  public void setTestDestination(@Nonnull final File destination) {
    this.testDestination = destination;
  }

  @Nonnull
  public String getInEncoding() {
    return this.inEncoding;
  }

  public void setInEncoding(@Nonnull final String value) {
    this.inEncoding = value;
  }

  @Nonnull
  public String getOutEncoding() {
    return this.outEncoding;
  }

  public void setOutEncoding(@Nonnull final String value) {
    this.outEncoding = value;
  }

  @Nonnull
  @MustNotContainNull
  public List<String> getExcluded() {
    return this.excluded;
  }

  public void setExcluded(@Nonnull @MustNotContainNull final List<String> excluded) {
    this.excluded = excluded;
  }

  public boolean getUnknownVarAsFalse() {
    return this.unknownVarAsFalse;
  }

  public void setUnknownVarAsFalse(final boolean flag) {
    this.unknownVarAsFalse = flag;
  }

  @Nonnull
  @MustNotContainNull
  public List<String> getProcessing() {
    return this.processing;
  }

  public void setProcessing(@Nonnull @MustNotContainNull final List<String> processing) {
    this.processing = processing;
  }

  public boolean getDisableOut() {
    return this.disableOut;
  }

  public void setDisableOut(final boolean value) {
    this.disableOut = value;
  }

  public boolean getVerbose() {
    return this.verbose;
  }

  public void setVerbose(final boolean verbose) {
    this.verbose = verbose;
  }

  public boolean getKeepLines() {
    return this.keepLines;
  }

  public void setKeepLines(final boolean keepLines) {
    this.keepLines = keepLines;
  }

  public boolean getAllowWhitespace() {
    return this.allowWhitespace;
  }

  public void setAllowWhitespace(final boolean flag) {
    this.allowWhitespace = flag;
  }

  public boolean getRemoveComments() {
    return this.removeComments;
  }

  public void setRemoveComments(final boolean value) {
    this.removeComments = value;
  }

  @Nonnull
  @MustNotContainNull
  private List<String> makeSourceRootList() {
    List<String> result = new ArrayList<>();
    if (this.source.isEmpty()) {
      if (this.project != null) {
        for (final String srcRoot : (this.getUseTestSources() ? this.testCompileSourceRoots : this.compileSourceRoots)) {
          final boolean folderPresented = new File(srcRoot).isDirectory();

          if (!folderPresented) {
            getLog().debug("Can't find source folder : " + srcRoot);
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

          if (textToAppend != null) {
            result.add(srcRoot);
          }
        }
      }
    } else {
      result.addAll(this.source);
    }
    return result;
  }

  private void replaceSourceRootByPreprocessingDestinationFolder(@Nonnull final PreprocessorContext context) throws IOException {
    if (this.project != null) {
      final List<PreprocessorContext.SourceFolder> sourceFolders = context.getSourceFolders();

      final List<String> sourceRoots = this.getUseTestSources() ? this.testCompileSourceRoots : this.compileSourceRoots;
      final List<String> sourceRootsAsCanonical = new ArrayList<>();
      for (final String src : sourceRoots) {
        sourceRootsAsCanonical.add(new File(src).getCanonicalPath());
      }

      for (final PreprocessorContext.SourceFolder folder : sourceFolders) {
        int index = sourceRoots.indexOf(folder.getAsString());
        if (index < 0) {
          // check for canonical paths
          final String canonicalPath = folder.getAsFile().getCanonicalPath();
          index = sourceRootsAsCanonical.indexOf(canonicalPath);
        }
        if (index >= 0) {
          info("Source root is removed from the source root list: " + sourceRoots.get(index));
          sourceRoots.remove(index);
        }
      }

      final String destinationDir = context.getDestinationDirectoryAsFile().getCanonicalPath();

      sourceRoots.add(destinationDir);
      info("Source root is enlisted: " + destinationDir);
    }
  }

  @Nonnull
  PreprocessorContext makePreprocessorContext(@Nonnull @MustNotContainNull final List<String> sourceFolders) throws IOException {
    final PreprocessorContext context = new PreprocessorContext();
    context.setPreprocessorLogger(this);

    if (this.project != null) {
      final MavenPropertiesImporter mavenPropertiesImporter = new MavenPropertiesImporter(context, project, getVerbose() || getLog().isDebugEnabled());
      context.registerSpecialVariableProcessor(mavenPropertiesImporter);
    }

    context.setSourceFolders(sourceFolders);
    context.setTargetFolder(assertNotNull(this.getUseTestSources() ? this.getTestDestination().getCanonicalPath() : this.getDestination().getCanonicalPath()));

    context.setInCharset(this.getInEncoding());
    context.setOutCharset(this.getOutEncoding());

    if (!this.getExcluded().isEmpty()) {
      context.setExcludedFileExtensions(this.getExcluded());
    }
    if (!this.getProcessing().isEmpty()) {
      context.setProcessingFileExtensions(this.getProcessing());
    }

    info("Source folders: " + context.getSourceFolders().stream().map(PreprocessorContext.SourceFolder::getAsString).collect(Collectors.joining(File.pathSeparator)));
    info(" Target folder: " + context.getTargetFolder());

    context.setUnknownVariableAsFalse(this.getUnknownVarAsFalse());
    context.setCompareDestination(this.isCompareDestination());
    context.setClearDestinationDirBefore(this.getClear());
    context.setCareForLastNextLine(this.getCarForLastNextLine());
    context.setRemoveComments(this.getRemoveComments());
    context.setVerbose(getLog().isDebugEnabled() || this.getVerbose());
    context.setKeepLines(this.getKeepLines());
    context.setFileOutputDisabled(this.getDisableOut());
    context.setAllowWhitespace(this.getAllowWhitespace());
    context.setPreserveIndent(this.getPreserveIndent());
    context.setExcludedFolderPatterns(this.getExcludedFolders().toArray(new String[0]));
    context.setCopyFileAttributes(this.getCopyFileAttributes());

    this.cfgFiles.forEach(x->context.addConfigFile(x));

    // process global vars
    this.getGlobalVars().forEach((key, value) -> {
      getLog().debug(String.format("Register global var: '%s' <- '%s'", key, value));
      context.setGlobalVariable(key, Value.recognizeRawString(value));
    });

    return context;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (this.isSkip()) {
      getLog().info("Skip preprocessing");
    } else {
      PreprocessorContext context;

      final List<String> sourceFoldersInPreprocessingFormat = makeSourceRootList();

      boolean skipPreprocessing = false;

      if (sourceFoldersInPreprocessingFormat.isEmpty()) {
        if (isIgnoreMissingSources()) {
          getLog().warn("Source folders are not provided, skip preprocessing.");
          skipPreprocessing = true;
        } else {
          throw new MojoFailureException("Can't find any source folder, check parameters and project type");
        }
      }

      if (!skipPreprocessing) {
        try {
          context = makePreprocessorContext(Assertions.assertNotNull(sourceFoldersInPreprocessingFormat));
        } catch (Exception ex) {
          final PreprocessorException newException = PreprocessorException.extractPreprocessorException(ex);
          throw new MojoExecutionException(newException == null ? ex.getMessage() : newException.toString(), newException == null ? ex : newException);
        }

        try {
          final JCPreprocessor preprocessor = new JCPreprocessor(context);
          preprocessor.execute();
          if (!getKeepSrcRoot()) {
            replaceSourceRootByPreprocessingDestinationFolder(context);
          }
        } catch (Exception ex) {
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
