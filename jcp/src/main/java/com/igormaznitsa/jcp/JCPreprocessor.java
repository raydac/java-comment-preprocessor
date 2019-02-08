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

import com.igormaznitsa.jcp.cmdline.AllowWhitespaceDirectiveHandler;
import com.igormaznitsa.jcp.cmdline.CareForLastNextLineCharHandler;
import com.igormaznitsa.jcp.cmdline.ClearDstDirectoryHandler;
import com.igormaznitsa.jcp.cmdline.CommandLineHandler;
import com.igormaznitsa.jcp.cmdline.CompareDestinationContentHandler;
import com.igormaznitsa.jcp.cmdline.CopyFileAttributesHandler;
import com.igormaznitsa.jcp.cmdline.DestinationDirectoryHandler;
import com.igormaznitsa.jcp.cmdline.ExcludeFoldersHandler;
import com.igormaznitsa.jcp.cmdline.ExcludedFileExtensionsHandler;
import com.igormaznitsa.jcp.cmdline.FileExtensionsHandler;
import com.igormaznitsa.jcp.cmdline.GlobalVariableDefiningFileHandler;
import com.igormaznitsa.jcp.cmdline.GlobalVariableHandler;
import com.igormaznitsa.jcp.cmdline.HelpHandler;
import com.igormaznitsa.jcp.cmdline.InCharsetHandler;
import com.igormaznitsa.jcp.cmdline.KeepLineHandler;
import com.igormaznitsa.jcp.cmdline.OutCharsetHandler;
import com.igormaznitsa.jcp.cmdline.PreserveIndentDirectiveHandler;
import com.igormaznitsa.jcp.cmdline.RemoveCommentsHandler;
import com.igormaznitsa.jcp.cmdline.SourceDirectoryHandler;
import com.igormaznitsa.jcp.cmdline.UnknownAsFalseHandler;
import com.igormaznitsa.jcp.cmdline.VerboseHandler;
import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.directives.ExcludeIfDirectiveHandler;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.expression.Expression;
import com.igormaznitsa.jcp.expression.Value;
import com.igormaznitsa.jcp.expression.ValueType;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import com.igormaznitsa.jcp.utils.antpathmatcher.AntPathMatcher;
import com.igormaznitsa.meta.annotation.MustNotContainNull;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.igormaznitsa.jcp.InfoHelper.makeTextForHelpInfo;
import static com.igormaznitsa.jcp.utils.PreprocessorUtils.readWholeTextFileIntoArray;
import static com.igormaznitsa.jcp.utils.PreprocessorUtils.throwPreprocessorException;
import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

/**
 * The main class implements the Java Comment Preprocessor, it has the main
 * method and can be started from a command string
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class JCPreprocessor {

  static final CommandLineHandler[] COMMAND_LINE_HANDLERS = new CommandLineHandler[] {
      new HelpHandler(),
      new InCharsetHandler(),
      new OutCharsetHandler(),
      new ClearDstDirectoryHandler(),
      new SourceDirectoryHandler(),
      new DestinationDirectoryHandler(),
      new FileExtensionsHandler(),
      new ExcludedFileExtensionsHandler(),
      new AllowWhitespaceDirectiveHandler(),
      new RemoveCommentsHandler(),
      new KeepLineHandler(),
      new CompareDestinationContentHandler(),
      new VerboseHandler(),
      new GlobalVariableDefiningFileHandler(),
      new GlobalVariableHandler(),
      new CareForLastNextLineCharHandler(),
      new PreserveIndentDirectiveHandler(),
      new ExcludeFoldersHandler(),
      new CopyFileAttributesHandler(),
      new UnknownAsFalseHandler()
  };
  private final PreprocessorContext context;

  public JCPreprocessor(@Nonnull final PreprocessorContext context) {
    assertNotNull("Configurator is null", context);
    this.context = context;
  }

  @Nonnull
  @MustNotContainNull
  public static List<CommandLineHandler> getCommandLineHandlers() {
    return Arrays.asList(COMMAND_LINE_HANDLERS);
  }

  public static void main(@Nonnull @MustNotContainNull final String... args) {
    printHeader();

    final String[] normalizedStrings = PreprocessorUtils.replaceStringPrefix(new String[] {"--", "-"}, "/", PreprocessorUtils.replaceChar(args, '$', '\"'));

    PreprocessorContext preprocessorContext = null;

    try {
      preprocessorContext = processCommandLine(args, normalizedStrings);
    } catch (Exception ex) {
      System.err.println("Error during CLI processing: " + ex.getMessage());
      System.exit(1);
    }

    final JCPreprocessor preprocessor = new JCPreprocessor(preprocessorContext);

    try {
      preprocessor.execute();
    } catch (Exception unexpected) {
      System.err.println(PreprocessorException.referenceAsString(' ', unexpected));
      System.exit(1);
    }

    System.exit(0);
  }

  @Nonnull
  private static PreprocessorContext processCommandLine(@Nonnull @MustNotContainNull final String[] originalStrings, @Nonnull @MustNotContainNull final String[] normalizedStrings) {
    final PreprocessorContext result = new PreprocessorContext();

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

  @Nonnull
  public PreprocessorContext getContext() {
    return context;
  }

  @Nonnull
  public PreprocessingStatistics execute() throws IOException {
    final long timeStart = System.currentTimeMillis();

    processCfgFiles();

    final List<PreprocessorContext.SourceFolder> srcFolders = context.getSourceFolders();
    final Collection<FileInfoContainer> filesToBePreprocessed = findAllFilesToBePreprocessed(srcFolders, context.getExcludedFolderPatterns());

    final List<PreprocessingState.ExcludeIfInfo> excludedIf = processGlobalDirectives(filesToBePreprocessed);

    processFileExclusion(excludedIf);
    if (!context.isFileOutputDisabled()) {
      createDestinationDirectory();
    }
    final PreprocessingStatistics stat = preprocessFiles(filesToBePreprocessed);

    final long elapsedTime = System.currentTimeMillis() - timeStart;
    this.context.logInfo("-----------------------------------------------------------------");
    this.context.logInfo(String.format("Preprocessed %d files, copied %d files, elapsed time %d ms", stat.getNumberOfPreprocessed(), stat.getNumberOfCopied(), elapsedTime));
    return stat;
  }

  private void processFileExclusion(@Nonnull @MustNotContainNull final List<PreprocessingState.ExcludeIfInfo> foundExcludeIf) {
    final String DIRECTIVE_NAME = new ExcludeIfDirectiveHandler().getFullName();

    for (final PreprocessingState.ExcludeIfInfo item : foundExcludeIf) {
      final String condition = item.getCondition();
      final File file = item.getFileInfoContainer().getSourceFile();

      Value val;

      if (context.isVerbose()) {
        context.logForVerbose(String.format("Processing condition '%s' for file '%s'", condition, file.getAbsolutePath()));
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
        throw new PreprocessorException("Expression at " + DIRECTIVE_NAME + " is not a boolean one", condition, new FilePositionInfo[] {new FilePositionInfo(file, item.getStringIndex())}, null);
      }

      if (val.asBoolean()) {
        item.getFileInfoContainer().setExcluded(true);
        if (context.isVerbose()) {
          context.logForVerbose(String.format("File '%s' excluded for active '%s' condition", file.getAbsolutePath(), condition));
        }
      }
    }
  }

  @Nonnull
  @MustNotContainNull
  private List<PreprocessingState.ExcludeIfInfo> processGlobalDirectives(@Nonnull @MustNotContainNull final Collection<FileInfoContainer> files) throws IOException {
    final List<PreprocessingState.ExcludeIfInfo> result = new ArrayList<>();
    for (final FileInfoContainer fileRef : files) {
      if (!(fileRef.isExcludedFromPreprocessing() || fileRef.isForCopyOnly())) {
        final long startTime = System.currentTimeMillis();
        result.addAll(fileRef.processGlobalDirectives(null, context));
        final long elapsedTime = System.currentTimeMillis() - startTime;
        if (context.isVerbose()) {
          context.logForVerbose(String.format("Global phase completed for file '%s', elapsed time %d ms ", PreprocessorUtils.getFilePath(fileRef.getSourceFile()), elapsedTime));
        }
      }
    }
    return result;
  }

  @Nonnull
  private PreprocessingStatistics preprocessFiles(@Nonnull @MustNotContainNull final Collection<FileInfoContainer> files) throws IOException {
    int prepFileCounter = 0;
    int copFileCounter = 0;
    for (final FileInfoContainer fileRef : files) {
      if (fileRef.isExcludedFromPreprocessing()) {
        // do nothing
      } else if (fileRef.isForCopyOnly()) {
        if (!context.isFileOutputDisabled()) {

          final File destinationFile = context.createDestinationFileForPath(fileRef.getDestinationFilePath());

          boolean doCopy = true;

          if (this.context.isCompareDestination() && PreprocessorUtils.isFileContentEquals(fileRef.getSourceFile(), destinationFile)) {
            doCopy = false;
            if (context.isVerbose()) {
              context.logForVerbose(String.format("Copy skipped because same content: %s -> {dst} %s", PreprocessorUtils.getFilePath(fileRef.getSourceFile()), fileRef.getDestinationFilePath()));
            }
          }

          if (doCopy) {
            if (context.isVerbose()) {
              context.logForVerbose(String.format("Copy file %s -> {dst} %s", PreprocessorUtils.getFilePath(fileRef.getSourceFile()), fileRef.getDestinationFilePath()));
            }
            PreprocessorUtils.copyFile(fileRef.getSourceFile(), destinationFile, context.isCopyFileAttributes());
            copFileCounter++;
          }
        }
      } else {
        final long startTime = System.currentTimeMillis();
        fileRef.preprocessFile(null, context);
        final long elapsedTime = System.currentTimeMillis() - startTime;
        if (context.isVerbose()) {
          context.logForVerbose(String.format("File preprocessing completed  '%s', elapsed time %d ms", PreprocessorUtils.getFilePath(fileRef.getSourceFile()), elapsedTime));
        }
        prepFileCounter++;
      }
    }
    return new PreprocessingStatistics(prepFileCounter, copFileCounter);
  }

  private void createDestinationDirectory() throws IOException {
    final File destination = context.getDestinationDirectoryAsFile();

    final boolean destinationExistsAndDirectory = destination.exists() && destination.isDirectory();

    if (context.doesClearDestinationDirBefore() && destinationExistsAndDirectory) {
      try {
        FileUtils.cleanDirectory(destination);
      } catch (IOException ex) {
        throw new IOException("Folder can't be cleaned: " + PreprocessorUtils.getFilePath(destination), ex);
      }
    }

    if (!destinationExistsAndDirectory && !destination.mkdirs()) {
      throw new IOException("Folder can't be created: " + PreprocessorUtils.getFilePath(destination));
    }
  }

  @Nonnull
  @MustNotContainNull
  private Collection<FileInfoContainer> findAllFilesToBePreprocessed(@Nonnull @MustNotContainNull final List<PreprocessorContext.SourceFolder> srcFolders, @Nonnull @MustNotContainNull final List<String> excludedFolderPatterns) throws IOException {
    final Collection<FileInfoContainer> result = new ArrayList<>();

    final AntPathMatcher antPathMatcher = new AntPathMatcher();

    for (final PreprocessorContext.SourceFolder dir : srcFolders) {
      String canonicalPathForSrcDirectory = dir.getAsFile().getCanonicalPath();

      if (!canonicalPathForSrcDirectory.endsWith(File.separator)) {
        canonicalPathForSrcDirectory += File.separator;
      }

      final Set<File> allFoundFiles = findAllFiles(canonicalPathForSrcDirectory, dir.getAsFile(), antPathMatcher, excludedFolderPatterns);

      for (final File file : allFoundFiles) {
        if (context.isFileExcludedFromProcess(file)) {
          // ignore excluded file
          continue;
        }

        final String filePath = file.getCanonicalPath();
        final String relativePath = filePath.substring(canonicalPathForSrcDirectory.length());

        final FileInfoContainer reference = new FileInfoContainer(file, relativePath, !this.context.isFileAllowedForPreprocessing(file));
        result.add(reference);
      }

    }

    return result;
  }

  @Nonnull
  private Set<File> findAllFiles(@Nonnull final String baseFolderCanonicalPath, @Nonnull final File dir, @Nonnull final AntPathMatcher antPathMatcher, @Nonnull @MustNotContainNull final List<String> excludedFolderPatterns) throws IOException {
    final Set<File> result = new HashSet<>();
    final File[] allowedFiles = dir.listFiles();
    if (allowedFiles != null) {
      final String normalizedBasePath = FilenameUtils.normalize(baseFolderCanonicalPath, true);

      for (final File file : allowedFiles) {
        if (file.isDirectory()) {
          boolean process = true;

          final String folderPath = file.getCanonicalPath();

          String excludingPattern = null;

          if (!excludedFolderPatterns.isEmpty()) {
            final String subPathInBase = folderPath.substring(normalizedBasePath.length());

            for (final String s : excludedFolderPatterns) {
              if (antPathMatcher.match(s, subPathInBase)) {
                excludingPattern = s;
                process = false;
                break;
              }
            }
          }

          if (process) {
            result.addAll(findAllFiles(baseFolderCanonicalPath, file, antPathMatcher, excludedFolderPatterns));
          } else {
            this.context.logForVerbose(String.format("Folder '%s' excluded for pattern '%s'", folderPath, excludingPattern));
          }
        } else {
          result.add(file);
        }
      }
    }
    return result;
  }

  void processCfgFiles() throws IOException {

    for (final File file : context.getConfigFiles()) {
      final String[] wholeFile = readWholeTextFileIntoArray(file, StandardCharsets.UTF_8, null);

      int readStringIndex = -1;
      for (final String curString : wholeFile) {
        final String trimmed = curString.trim();
        readStringIndex++;

        if (trimmed.isEmpty() || trimmed.charAt(0) == '#') {
          // do nothing
        } else if (trimmed.charAt(0) == '@') {
          throwPreprocessorException("You can't start any string in a global variable defining file with \'@\'", trimmed, file, readStringIndex, null);
        } else if (trimmed.charAt(0) == '/') {
          // a command line argument
          boolean processed = false;
          try {
            for (CommandLineHandler handler : getCommandLineHandlers()) {
              if (context.isVerbose()) {
                context.logForVerbose(String.format("Processing сonfig file key '%s' at %s: %d", trimmed, file.getName(), readStringIndex + 1));
              }
              if (handler.processCommandLineKey(trimmed, context)) {
                processed = true;
                break;
              }
            }
          } catch (Exception unexpected) {
            throwPreprocessorException("Exception during directive processing", trimmed, file, readStringIndex, unexpected);
          }

          if (!processed) {
            throwPreprocessorException("Unsupported or disallowed directive", trimmed, file, readStringIndex, null);
          }
        } else {
          // a global variable
          final String[] split = PreprocessorUtils.splitForEqualChar(trimmed);
          if (split.length != 2) {
            throwPreprocessorException("Wrong variable definition", trimmed, file, readStringIndex, null);
          }
          final String name = split[0].trim().toLowerCase(Locale.ENGLISH);
          final String expression = split[1].trim();
          if (name.isEmpty()) {
            throwPreprocessorException("Empty variable name detected", trimmed, file, readStringIndex, null);
          }

          try {
            final Value result = Expression.evalExpression(expression, this.context);
            this.context.setGlobalVariable(name, result);

            if (this.context.isVerbose()) {
              this.context.logForVerbose(String.format("Register global variable '%s' = '%s' (%s:%d)", name, result.toString(), file.getName(), readStringIndex + 1));
            }
          } catch (Exception unexpected) {
            throwPreprocessorException("Can't process the global variable definition", trimmed, file, readStringIndex, unexpected);
          }
        }
      }
    }
  }

  public static final class PreprocessingStatistics {

    private final int numberOfPreprocessed;
    private final int numberOfCopied;

    public PreprocessingStatistics(final int numberOfPreprocessed, final int numberOfCopied) {
      this.numberOfPreprocessed = numberOfPreprocessed;
      this.numberOfCopied = numberOfCopied;
    }

    public int getNumberOfCopied() {
      return this.numberOfCopied;
    }

    public int getNumberOfPreprocessed() {
      return this.numberOfPreprocessed;
    }
  }
}
