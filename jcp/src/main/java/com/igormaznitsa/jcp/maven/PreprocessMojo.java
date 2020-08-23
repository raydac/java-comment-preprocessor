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

import static com.igormaznitsa.meta.common.utils.GetUtils.ensureNonNull;


import com.igormaznitsa.jcp.JcpPreprocessor;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.logger.PreprocessorLogger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Mojo to preprocess either standard maven project source roots or custom source roots and place prepsocessed result into defined target folder.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Mojo(name = "preprocess", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class PreprocessMojo extends AbstractMojo implements PreprocessorLogger {

  /**
   * Maven project source roots for compilation phase.
   */
  @Setter(AccessLevel.NONE)
  @Parameter(alias = "compileSourceRoots", defaultValue = "${project.compileSourceRoots}", required = true, readonly = true)
  private List<String> compileSourceRoots = new ArrayList<>();

  /**
   * Maven project test source roots for test phase.
   */
  @Setter(AccessLevel.NONE)
  @Parameter(alias = "testCompileSourceRoots", defaultValue = "${project.testCompileSourceRoots}", required = true, readonly = true)
  private List<String> testCompileSourceRoots = new ArrayList<>();

  /**
   * Maven project to be preprocessed.
   */
  @Setter(AccessLevel.NONE)
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  /**
   * Source root folders for preprocessing, if it is empty then project provided folders will be used.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "sources")
  private List<String> sources = null;

  /**
   * End of line string to be used in reprocessed results. It supports java escaping chars.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "eol", property = "jcp.line.separator", defaultValue = "${line.separator}")
  private String eol = null;

  /**
   * Keep attributes for preprocessing file and copy them to result one.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "keepAttributes", defaultValue = "false")
  private boolean keepAttributes = false;

  /**
   * Target folder to place preprocessing result in regular source processing phase.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "target", defaultValue = "${project.build.directory}${file.separator}generated-sources${file.separator}preprocessed")
  private File target = null;

  /**
   * Target folder to place preprocessing result in test source processing phase.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "targetTest", defaultValue = "${project.build.directory}${file.separator}generated-test-sources${file.separator}preprocessed")
  private File targetTest = null;

  /**
   * Encoding for text read operations.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "sourceEncoding", defaultValue = "${project.build.sourceEncoding}")
  private String sourceEncoding = StandardCharsets.UTF_8.name();

  /**
   * Encoding for text write operations.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "targetEncoding", defaultValue = "${project.build.sourceEncoding}")
  private String targetEncoding = StandardCharsets.UTF_8.name();

  /**
   * Flag to ignore missing source folders, if false then mojo fail for any missing source folder, if true then missing folder will be ignored.
   *
   * @since 6.1.1
   */
  @Parameter(alias = "ignoreMissingSources", defaultValue = "false")
  private boolean ignoreMissingSources = false;

  /**
   * List of file extensions to be excluded from preprocessing. By default excluded xml.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "excludeExtensions")
  private List<String> excludeExtensions = Collections.singletonList("xml");

  /**
   * List of file extensions to be included into preprocessing. By default java,txt,htm,html
   *
   * @since 7.0.0
   */
  @Parameter(alias = "extensions")
  private List<String> extensions = new ArrayList<>(Arrays.asList("java", "txt", "htm", "html"));

  /**
   * Recognize a unknown variable as containing boolean false flag.
   */
  @Parameter(alias = "unknownVarAsFalse", defaultValue = "false")
  private boolean unknownVarAsFalse = false;

  /**
   * Dry run, making preprocessing but without output
   *
   * @since 7.0.0.
   */
  @Parameter(alias = "dryRun", defaultValue = "false")
  private boolean dryRun = false;

  /**
   * Verbose mode.
   */
  @Parameter(alias = "verbose", defaultValue = "false")
  private boolean verbose = false;

  /**
   * Clear target folder if it exists.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "clearTarget", defaultValue = "false")
  private boolean clearTarget = false;

  /**
   * Set base directory which will be used for relative source paths.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "baseDir", defaultValue = "${project.basedir}")
  private File baseDir = new File(".");

  /**
   * Carefully reproduce last EOL in result files.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "careForLastEol", defaultValue = "false")
  private boolean careForLastEol = false;

  /**
   * Replace source root folders in maven project after preprocessing for following processing.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "replaceSources", defaultValue = "true")
  private boolean replaceSources = true;

  /**
   * Keep comments in result files.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "keepComments", defaultValue = "true")
  private boolean keepComments = true;

  /**
   * List of variables to be registered in preprocessor as global ones.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "vars")
  private Map<String, String> vars = new HashMap<>();

  /**
   * List of patterns of folder paths to be excluded from preprocessing, It uses ANT path pattern format.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "excludeFolders")
  private List<String> excludeFolders = new ArrayList<>();

  /**
   * List of external files containing variable definitions.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "configFiles")
  private List<String> configFiles = new ArrayList<>();

  /**
   * Keep preprocessing directives in result files as commented ones, it is useful to not break line numeration in result files.
   */
  @Parameter(alias = "keepLines", defaultValue = "true")
  private boolean keepLines = true;

  /**
   * Turn on support of white spaces in preprocessor directives between '//' and the '#'.
   */
  @Parameter(alias = "allowWhitespaces", defaultValue = "false")
  private boolean allowWhitespaces = false;

  /**
   * Preserve indents in lines marked by '//$' and '//$$' directives. Directives will be replaced by white spaces chars.
   */
  @Parameter(alias = "preserveIndents", defaultValue = "false")
  private boolean preserveIndents = false;

  /**
   * Turn on test sources root use.
   *
   * @since 5.3.4
   */
  @Parameter(alias = "useTestSources", defaultValue = "false")
  private boolean useTestSources = false;

  /**
   * Skip preprocessing. Also can be defined by property 'jcp.preprocess.skip'
   *
   * @since 6.1.1
   */
  @Parameter(alias = "skip", property = "jcp.preprocess.skip", defaultValue = "false")
  private boolean skip = false;

  /**
   * Turn on check of content body compare with existing result file to prevent overwriting, if content is the same then preprocessor will not be writing new result content.
   *
   * @since 7.0.0
   */
  @Parameter(alias = "dontOverwriteSameContent", defaultValue = "false")
  private boolean dontOverwriteSameContent = false;


  private List<String> formSourceRootList() {
    List<String> result = Collections.emptyList();
    if (this.getSources() == null) {
      if (this.project != null) {
        result = (this.isUseTestSources() ? this.testCompileSourceRoots : this.compileSourceRoots)
            .stream()
            .filter(Objects::nonNull)
            .map(File::new)
            .peek(x -> {
              if (!x.isDirectory()) {
                getLog().debug(String.format("Src.folder doesn't exist: %s", x));
              }
            })
            .filter(x -> !this.isIgnoreMissingSources() || x.isDirectory())
            .map(File::getAbsolutePath)
            .collect(Collectors.toList());
      }
    } else {
      result = new ArrayList<>(this.getSources());
    }
    return result;
  }

  private void replaceSourceRootByPreprocessingDestinationFolder(final PreprocessorContext context)
      throws IOException {
    if (this.project != null) {
      final List<PreprocessorContext.SourceFolder> sourceFolders = context.getSources();

      final List<String> sourceRoots =
          this.isUseTestSources() ? this.testCompileSourceRoots : this.compileSourceRoots;
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

      final String destinationDir = context.getTarget().getCanonicalPath();

      sourceRoots.add(destinationDir);
      info("Source root is enlisted: " + destinationDir);
    }
  }


  PreprocessorContext makePreprocessorContext() throws IOException {
    final PreprocessorContext context = new PreprocessorContext(this.getBaseDir());
    context.setPreprocessorLogger(this);

    if (this.project != null) {
      final MavenPropertiesImporter mavenPropertiesImporter =
          new MavenPropertiesImporter(context, project, isVerbose() || getLog().isDebugEnabled());
      context.registerSpecialVariableProcessor(mavenPropertiesImporter);
    }

    context.setSources(formSourceRootList());
    context.setTarget((this.isUseTestSources() ? this.getTargetTest() : this.getTarget()));

    context.setSourceEncoding(Charset.forName(this.getSourceEncoding().trim()));
    context.setTargetEncoding(Charset.forName(this.getTargetEncoding().trim()));

    context.setExcludeFolders(this.getExcludeFolders());
    context.setExcludeExtensions(this.getExcludeExtensions());
    context.setExtensions(this.getExtensions());

    if (this.getEol() != null) {
      context.setEol(StringEscapeUtils.unescapeJava(this.getEol()));
    }

    info("Source folders: " +
        context.getSources().stream().map(PreprocessorContext.SourceFolder::getAsString)
            .collect(Collectors.joining(File.pathSeparator)));
    info("Target folder: " + context.getTarget());

    context.setUnknownVariableAsFalse(this.isUnknownVarAsFalse());
    context.setDontOverwriteSameContent(this.isDontOverwriteSameContent());
    context.setClearTarget(this.isClearTarget());
    context.setCareForLastEol(this.isCareForLastEol());
    context.setKeepComments(this.isKeepComments());
    context.setVerbose(getLog().isDebugEnabled() || this.isVerbose());
    context.setKeepLines(this.isKeepLines());
    context.setDryRun(this.isDryRun());
    context.setAllowWhitespaces(this.isAllowWhitespaces());
    context.setPreserveIndents(this.isPreserveIndents());
    context.setExcludeFolders(this.getExcludeFolders());
    context.setKeepAttributes(this.isKeepAttributes());

    this.configFiles.forEach(x -> context.registerConfigFile(new File(x)));

    // process global vars
    this.getVars().forEach((key, value) -> {
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
      final PreprocessorContext context;
      try {
        context = makePreprocessorContext();
      } catch (Exception ex) {
        final PreprocessorException newException =
            PreprocessorException.extractPreprocessorException(ex);
        throw new MojoExecutionException(
            newException == null ? ex.getMessage() : newException.toString(),
            newException == null ? ex : newException);
      }

      if (context.getSources().isEmpty()) {
        if (this.isIgnoreMissingSources()) {
          getLog().warn("Source folders are not provided, preprocessing is ignored.");
        } else {
          throw new MojoFailureException(
              "Source folders are not provided, check parameters and project type");
        }
      } else {
        try {
          final JcpPreprocessor preprocessor = new JcpPreprocessor(context);
          preprocessor.execute();
          if (this.isReplaceSources()) {
            replaceSourceRootByPreprocessingDestinationFolder(context);
          }
        } catch (Exception ex) {
          final PreprocessorException pp = PreprocessorException.extractPreprocessorException(ex);
          throw new MojoFailureException(
              pp == null ? ex.getMessage() : PreprocessorException.referenceAsString('.', pp),
              pp == null ? ex : pp);
        }
      }
    }
  }

  @Override
  public void error(final String message) {
    getLog().error(ensureNonNull(message, "<null>"));
  }

  @Override
  public void info(final String message) {
    getLog().info(ensureNonNull(message, "<null>"));
  }

  @Override
  public void warning(final String message) {
    getLog().warn(ensureNonNull(message, "<null>"));
  }

  @Override
  public void debug(final String message) {
    getLog().debug(ensureNonNull(message, "<null>"));
  }
}
