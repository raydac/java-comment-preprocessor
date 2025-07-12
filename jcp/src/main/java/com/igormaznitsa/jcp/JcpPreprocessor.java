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

package com.igormaznitsa.jcp;

import static com.igormaznitsa.jcp.InfoHelper.makeTextForHelpInfo;
import static com.igormaznitsa.jcp.utils.PreprocessorUtils.findAndInstantiateAllServices;
import static com.igormaznitsa.jcp.utils.PreprocessorUtils.readWholeTextFileIntoArray;
import static com.igormaznitsa.jcp.utils.PreprocessorUtils.throwPreprocessorException;

import com.igormaznitsa.jcp.cmdline.ActionPreprocessorExtensionHandler;
import com.igormaznitsa.jcp.cmdline.AllowMergeBlockLineHandler;
import com.igormaznitsa.jcp.cmdline.AllowWhitespaceDirectiveHandler;
import com.igormaznitsa.jcp.cmdline.CareForLastEolHandler;
import com.igormaznitsa.jcp.cmdline.ClearTargetHandler;
import com.igormaznitsa.jcp.cmdline.CommandLineHandler;
import com.igormaznitsa.jcp.cmdline.DestinationDirectoryHandler;
import com.igormaznitsa.jcp.cmdline.DontOverwriteSameContentHandler;
import com.igormaznitsa.jcp.cmdline.ExcludeFoldersHandler;
import com.igormaznitsa.jcp.cmdline.ExcludedFileExtensionsHandler;
import com.igormaznitsa.jcp.cmdline.FileExtensionsHandler;
import com.igormaznitsa.jcp.cmdline.GlobalVariableDefiningFileHandler;
import com.igormaznitsa.jcp.cmdline.GlobalVariableHandler;
import com.igormaznitsa.jcp.cmdline.HelpHandler;
import com.igormaznitsa.jcp.cmdline.InCharsetHandler;
import com.igormaznitsa.jcp.cmdline.KeepAttributesHandler;
import com.igormaznitsa.jcp.cmdline.KeepCommentsHandler;
import com.igormaznitsa.jcp.cmdline.KeepLineHandler;
import com.igormaznitsa.jcp.cmdline.OutCharsetHandler;
import com.igormaznitsa.jcp.cmdline.PreserveIndentDirectiveHandler;
import com.igormaznitsa.jcp.cmdline.RemoveCommentsHandler;
import com.igormaznitsa.jcp.cmdline.SourceDirectoryHandler;
import com.igormaznitsa.jcp.cmdline.UnknownAsFalseHandler;
import com.igormaznitsa.jcp.cmdline.VerboseHandler;
import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.context.CommentTextProcessor;
import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.context.SpecialVariableProcessor;
import com.igormaznitsa.jcp.directives.ExcludeIfDirectiveHandler;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.utils.AntPathMatcher;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * The main class implements the Java Comment Preprocessor, it has the main
 * method and can be started from a command string
 * Base directory for preprocessing can be provided through System property 'jcp.base.dir'
 * if it is not provided then current work directory will be recognized as base one.
 */
public final class JcpPreprocessor {

  static final CommandLineHandler[] COMMAND_LINE_HANDLERS = new CommandLineHandler[] {
      new HelpHandler(),
      new InCharsetHandler(),
      new OutCharsetHandler(),
      new ClearTargetHandler(),
      new SourceDirectoryHandler(),
      new DestinationDirectoryHandler(),
      new FileExtensionsHandler(),
      new ExcludedFileExtensionsHandler(),
      new AllowWhitespaceDirectiveHandler(),
      new RemoveCommentsHandler(),
      new KeepCommentsHandler(),
      new KeepLineHandler(),
      new DontOverwriteSameContentHandler(),
      new VerboseHandler(),
      new GlobalVariableDefiningFileHandler(),
      new GlobalVariableHandler(),
      new CareForLastEolHandler(),
      new PreserveIndentDirectiveHandler(),
      new ExcludeFoldersHandler(),
      new KeepAttributesHandler(),
      new ActionPreprocessorExtensionHandler(),
      new AllowMergeBlockLineHandler(),
      new UnknownAsFalseHandler()
  };
  private static final String PROPERTY_JCP_BASE_DIR = "jcp.base.dir";
  private final PreprocessorContext context;

  public JcpPreprocessor(final PreprocessorContext context) {
    Objects.requireNonNull(context, "Configurator is null");
    this.context = context;
  }


  public static List<CommandLineHandler> getCommandLineHandlers() {
    return Arrays.asList(COMMAND_LINE_HANDLERS);
  }

  public static void main(final String... args) {
    printHeader();

    final String[] normalizedStrings = PreprocessorUtils
        .replaceStringPrefix(new String[] {"--", "-"}, "/",
            PreprocessorUtils.replaceChar(args, '$', '\"'));

    PreprocessorContext preprocessorContext = null;

    final File baseDir = getBaseDir();
    System.out.println("Base directory: " + baseDir);

    try {
      preprocessorContext = processCommandLine(baseDir, args, normalizedStrings);
    } catch (Exception ex) {
      System.err.println("Error during CLI processing: " + ex.getMessage());
      System.exit(1);
    }

    final JcpPreprocessor preprocessor = new JcpPreprocessor(preprocessorContext);

    try {
      preprocessor.execute();
    } catch (Exception unexpected) {
      System.err.println(PreprocessorException.referenceAsString(' ', unexpected));
      System.exit(1);
    }

    System.exit(0);
  }


  private static File getBaseDir() {
    final String baseDirInProperties = System
        .getProperty(PROPERTY_JCP_BASE_DIR,
            System.getProperty("user.dir", new File("").getAbsolutePath())
        );
    return new File(baseDirInProperties);
  }


  private static PreprocessorContext processCommandLine(final File baseDir,
                                                        final String[] originalStrings,
                                                        final String[] normalizedStrings) {
    final PreprocessorContext result = new PreprocessorContext(baseDir);

    for (int i = 0; i < normalizedStrings.length; i++) {
      final String arg = normalizedStrings[i];
      boolean processed = false;
      for (final CommandLineHandler processor : getCommandLineHandlers()) {
        if (processor.processCommandLineKey(arg, result)) {
          processed = true;
          if (processor instanceof HelpHandler) {
            help();
            System.exit(2);
          }
          break;
        }
      }

      if (!processed) {
        System.err.println("Can't process CLI argument, see manual: " + originalStrings[i]);
        System.out.println();
        help();
        System.exit(1);
      }
    }

    final List<CommentTextProcessor> commentTextProcessors = findAndInstantiateAllServices(
        CommentTextProcessor.class);
    if (!commentTextProcessors.isEmpty()) {
      System.out.printf("Detected comment text processors: %s%n",
          commentTextProcessors.stream().map(x -> x.getClass().getCanonicalName())
              .collect(Collectors.joining(",")));
      commentTextProcessors.forEach(result::addCommentTextProcessor);
    }

    return result;
  }

  private static void printHeader() {
    System.out.println(InfoHelper.getProductName() + ' ' + InfoHelper.getVersion());
    System.out.println(InfoHelper.getSite());
    System.out.println(InfoHelper.getCopyright());
  }

  private static void help() {
    System.out.println();

    makeTextForHelpInfo().forEach(System.out::println);
  }


  public PreprocessorContext getContext() {
    return this.context;
  }


  public Statistics execute() throws IOException {
    this.context.getCommentTextProcessors().forEach(x -> x.onContextStarted(this.context));
    this.context.getMapVariableNameToSpecialVarProcessor()
        .values().forEach(x -> x.onContextStarted(this.context));

    final long timeStart = System.currentTimeMillis();
    Throwable throwable = null;
    Statistics stat = null;
    try {
      this.context.getActivatedConfigFiles().addAll(processConfigFiles());

      this.context.logInfo(String
          .format("File extensions: %s excluded %s", this.context.getExtensions(),
              this.context.getExcludeExtensions()));
      final List<PreprocessorContext.SourceFolder> srcFolders = this.context.getSources();
      this.context.logDebug("Source folders: " + srcFolders);

      if (srcFolders.isEmpty()) {
        this.context.logWarning("Source folder list is empty!");
      }

      final Collection<FileInfoContainer> filesToBePreprocessed =
          collectFilesToPreprocess(srcFolders, this.context.getExcludeFolders());
      this.context.addAllPreprocessedResources(filesToBePreprocessed);

      final List<PreprocessingState.ExcludeIfInfo> excludedIf =
          processGlobalDirectives(filesToBePreprocessed);

      processFileExclusion(excludedIf);
      if (!this.context.isDryRun()) {
        createTargetFolder();
      } else {
        this.context.logInfo("Dry run mode is ON");
      }
      stat = preprocessFiles(filesToBePreprocessed);
    } catch (Throwable ex) {
      throwable = ex;
      if (ex instanceof IOException) {
        throw (IOException) ex;
      }
    } finally {
      try {
        for (final CommentTextProcessor p : this.context.getCommentTextProcessors()) {
          p.onContextStopped(this.context, throwable);
        }
      } finally {
        for (final SpecialVariableProcessor p : this.context.getMapVariableNameToSpecialVarProcessor()
            .values()) {
          p.onContextStopped(this.context, throwable);
        }
      }
    }
    if (stat != null) {
      final long elapsedTime = System.currentTimeMillis() - timeStart;
      this.context.logInfo("-----------------------------------------------------------------");
      this.context.logInfo(String
          .format("Preprocessed %d files, copied %d files, ignored %d files, elapsed time %d ms",
              stat.getPreprocessed(), stat.getCopied(), stat.getExcluded(), elapsedTime));
    }
    return stat;

  }

  private void processFileExclusion(final List<PreprocessingState.ExcludeIfInfo> foundExcludeIf) {
    final String DIRECTIVE_NAME = new ExcludeIfDirectiveHandler().getFullName();

    for (final PreprocessingState.ExcludeIfInfo item : foundExcludeIf) {
      final String condition = item.getCondition();
      final File file = item.getFileInfoContainer().getSourceFile();

      Value val;

      if (context.isVerbose()) {
        context.logForVerbose(String
            .format("Processing condition '%s' for file '%s'", condition, file.getAbsolutePath()));
      }

      try {
        val = Expression.evalExpression(condition, this.context);
      } catch (PreprocessorException ex) {
        throw new PreprocessorException(
            ex.getMessage(),
            condition,
            new FilePositionInfo[] {new FilePositionInfo(file, item.getStringIndex())},
            ex.getCause()
        );
      } catch (IllegalArgumentException ex) {
        throw new PreprocessorException("Wrong expression at " + DIRECTIVE_NAME,
            condition,
            new FilePositionInfo[] {new FilePositionInfo(file, item.getStringIndex())},
            ex);
      }

      if (val.getType() != ValueType.BOOLEAN) {
        throw new PreprocessorException("Expression at " + DIRECTIVE_NAME + " is not a boolean one",
            condition, new FilePositionInfo[] {new FilePositionInfo(file, item.getStringIndex())},
            null);
      }

      if (val.asBoolean()) {
        item.getFileInfoContainer().setExcluded(true);
        if (context.isVerbose()) {
          context.logForVerbose(String
              .format("File '%s' excluded for active '%s' condition", file.getAbsolutePath(),
                  condition));
        }
      }
    }
  }


  private List<PreprocessingState.ExcludeIfInfo> processGlobalDirectives(
      final Collection<FileInfoContainer> files) throws IOException {
    final List<PreprocessingState.ExcludeIfInfo> result = new ArrayList<>();
    for (final FileInfoContainer fileRef : files) {
      if (!(fileRef.isExcludedFromPreprocessing() || fileRef.isCopyOnly())) {
        final long startTime = System.currentTimeMillis();
        result.addAll(fileRef.processGlobalDirectives(null, context));
        final long elapsedTime = System.currentTimeMillis() - startTime;
        if (context.isVerbose()) {
          context.logForVerbose(String
              .format("Global phase completed for file '%s', elapsed time %d ms ",
                  PreprocessorUtils.getFilePath(fileRef.getSourceFile()), elapsedTime));
        }
      }
    }
    return result;
  }


  private Statistics preprocessFiles(final Collection<FileInfoContainer> files) throws IOException {
    int preprocessedCounter = 0;
    int copiedCounter = 0;
    int excludedCounter = 0;

    for (final FileInfoContainer fileRef : files) {
      if (fileRef.isExcludedFromPreprocessing()) {
        excludedCounter++;
      } else if (fileRef.isCopyOnly()) {
        if (!context.isDryRun()) {
          final File destinationFile =
              this.context.createDestinationFileForPath(fileRef.makeTargetFilePathAsString());
          boolean doCopy = true;

          if (this.context.isDontOverwriteSameContent() &&
              PreprocessorUtils.isFileContentEquals(fileRef.getSourceFile(), destinationFile)) {
            doCopy = false;
            if (this.context.isVerbose()) {
              this.context.logForVerbose(String
                  .format("Copy skipped because same content: %s -> {dst} %s",
                      PreprocessorUtils.getFilePath(fileRef.getSourceFile()),
                      fileRef.makeTargetFilePathAsString()));
            }
          }

          if (doCopy) {
            if (this.context.isVerbose()) {
              this.context.logForVerbose(String.format("Copy file %s -> {dst} %s",
                  PreprocessorUtils.getFilePath(fileRef.getSourceFile()),
                  fileRef.makeTargetFilePathAsString()));
            }
            PreprocessorUtils.copyFile(fileRef.getSourceFile(), destinationFile,
                this.context.isKeepAttributes());
            fileRef.getGeneratedResources().add(destinationFile);
            copiedCounter++;
          }
        }
      } else {
        final long startTime = System.currentTimeMillis();
        fileRef.preprocessFile(null, this.context);
        final long elapsedTime = System.currentTimeMillis() - startTime;
        if (this.context.isVerbose()) {
          this.context.logForVerbose(String
              .format("File preprocessing completed  '%s', elapsed time %d ms",
                  PreprocessorUtils.getFilePath(fileRef.getSourceFile()), elapsedTime));
        }
        preprocessedCounter++;
      }
    }

    return new Statistics(
        preprocessedCounter,
        copiedCounter,
        excludedCounter
    );
  }

  private void createTargetFolder() throws IOException {
    final File target = context.getTarget();

    final boolean targetExists = target.isDirectory();

    if (context.isClearTarget() && targetExists) {
      this.context.logForVerbose("Cleaining target folder: " + target);
      try {
        FileUtils.cleanDirectory(target);
      } catch (IOException ex) {
        throw new IOException("Can't clean folder: " + PreprocessorUtils.getFilePath(target), ex);
      }
    }

    if (!targetExists && !target.mkdirs()) {
      throw new IOException("Can't make folder: " + PreprocessorUtils.getFilePath(target));
    }

    this.context.logForVerbose("Target folder has been prepared: " + target);
  }


  private Collection<FileInfoContainer> collectFilesToPreprocess(
      final List<PreprocessorContext.SourceFolder> sources, final List<String> excluded)
      throws IOException {
    final Collection<FileInfoContainer> result = new ArrayList<>();

    final AntPathMatcher antPathMatcher = new AntPathMatcher();

    for (final PreprocessorContext.SourceFolder sourceFolder : sources) {
      String canonicalSourcePath = sourceFolder.getAsFile().getCanonicalPath();

      this.context.logDebug("Processing folder: " + sourceFolder);

      if (!canonicalSourcePath.endsWith(File.separator)) {
        canonicalSourcePath += File.separator;
      }

      for (final File file : findAllFiles(canonicalSourcePath, sourceFolder.getAsFile(),
          antPathMatcher, excluded)) {
        if (this.context.isFileExcludedByExtension(file)) {
          this.context
              .logForVerbose(String.format("File '%s' excluded by its extension", file.getPath()));
        } else {
          final String canonicalFilePath = file.getCanonicalPath();
          final String canonicalRelativePath =
              canonicalFilePath.substring(canonicalSourcePath.length());
          final FileInfoContainer reference = new FileInfoContainer(file, canonicalRelativePath,
              !this.context.isFileAllowedForPreprocessing(file));
          result.add(reference);
          this.context.logDebug("File added to preprocess list: " + reference);
        }
      }
    }

    return result;
  }


  private Set<File> findAllFiles(
      final String sourceCanonicalPath,
      final File dir,
      final AntPathMatcher antPathMatcher,
      final List<String> excludedFolderPatterns
  ) throws IOException {

    final Set<File> result = new HashSet<>();

    this.context.logDebug("Looking for files in folder: " + dir);

    final File[] allowedFiles = dir.listFiles();
    if (allowedFiles == null) {
      this.context.logWarning("Can't find files in folder: " + dir);
    } else {
      final String normalizedBasePath = FilenameUtils.normalize(sourceCanonicalPath, true);

      for (final File file : allowedFiles) {
        if (file.isDirectory()) {
          final String folderPath = file.getCanonicalPath();
          String excludedFolderPattern = null;

          if (!excludedFolderPatterns.isEmpty()) {
            final String subPathInBase = folderPath.substring(normalizedBasePath.length());

            for (final String pattern : excludedFolderPatterns) {
              if (antPathMatcher.match(pattern, subPathInBase)) {
                excludedFolderPattern = pattern;
                break;
              }
            }
          }

          if (excludedFolderPattern == null) {
            result.addAll(
                findAllFiles(sourceCanonicalPath, file, antPathMatcher, excludedFolderPatterns));
          } else {
            this.context.logForVerbose(
                String.format("Folder '%s' excluded by '%s'", folderPath, excludedFolderPattern));
          }
        } else {
          result.add(file);
        }
      }
    }
    return result;
  }

  List<File> processConfigFiles() throws IOException {

    final List<File> processedConfigFileList = new ArrayList<>();

    for (final File file : context.getConfigFiles()) {
      processedConfigFileList.add(file);

      final String[] lines = readWholeTextFileIntoArray(file, StandardCharsets.UTF_8, null);

      int readStringIndex = -1;
      for (final String curString : lines) {
        final String trimmed = curString.trim();
        readStringIndex++;

        if (trimmed.isEmpty() || trimmed.charAt(0) == '#') {
          // do nothing
        } else if (trimmed.charAt(0) == '@') {
          throwPreprocessorException("Config file doesn't allow have lines started with '@'",
              trimmed, file, readStringIndex, null);
        } else if (trimmed.charAt(0) == '/') {
          // a command line argument
          boolean processed = false;
          try {
            for (CommandLineHandler handler : getCommandLineHandlers()) {
              if (context.isVerbose()) {
                context.logForVerbose(String
                    .format("Processing сonfig file key '%s' at %s:%d", trimmed, file.getName(),
                        readStringIndex + 1));
              }
              if (handler.processCommandLineKey(trimmed, context)) {
                processed = true;
                break;
              }
            }
          } catch (Exception unexpected) {
            throwPreprocessorException("Exception during directive processing", trimmed, file,
                readStringIndex, unexpected);
          }

          if (!processed) {
            throwPreprocessorException("Unsupported or disallowed directive", trimmed, file,
                readStringIndex, null);
          }
        } else {
          // a global variable
          final String[] split = PreprocessorUtils.splitForEqualChar(trimmed);
          if (split.length != 2) {
            throwPreprocessorException("Wrong variable definition", trimmed, file, readStringIndex,
                null);
          }
          final String name = split[0].trim().toLowerCase(Locale.ENGLISH);
          final String expression = split[1].trim();
          if (name.isEmpty()) {
            throwPreprocessorException("Empty variable name detected", trimmed, file,
                readStringIndex, null);
          }

          try {
            final Value result = Expression.evalExpression(expression, this.context);
            this.context.setGlobalVariable(name, result);

            if (this.context.isVerbose()) {
              this.context.logForVerbose(String
                  .format("Registering global variable '%s' = '%s' (%s:%d)", name,
                      result.toString(), file.getName(), readStringIndex + 1));
            }
          } catch (Exception unexpected) {
            throwPreprocessorException("Can't process the global variable definition", trimmed,
                file, readStringIndex, unexpected);
          }
        }
      }
    }
    return processedConfigFileList;
  }

  @Data
  public static final class Statistics {
    private final int preprocessed;
    private final int copied;
    private final int excluded;
  }
}
