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
package com.igormaznitsa.jcp;

import com.igormaznitsa.jcp.cmdline.*;
import com.igormaznitsa.jcp.containers.FileInfoContainer;
import com.igormaznitsa.jcp.context.*;
import com.igormaznitsa.jcp.directives.*;
import com.igormaznitsa.jcp.exceptions.*;
import com.igormaznitsa.jcp.expression.*;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;

import java.io.*;
import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import com.igormaznitsa.meta.annotation.MustNotContainNull;
import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;
import org.apache.commons.io.FilenameUtils;
import com.igormaznitsa.jcp.utils.antpathmatcher.AntPathMatcher;

/**
 * The main class implements the Java Comment Preprocessor, it has the main
 * method and can be started from a command string
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public final class JCPreprocessor {

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

  private final PreprocessorContext context;
  static final CommandLineHandler[] COMMAND_LINE_HANDLERS = new CommandLineHandler[]{
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

  @Nonnull
  public static Iterable<CommandLineHandler> getCommandLineHandlers() {
    return Arrays.asList(COMMAND_LINE_HANDLERS);
  }

  @Nonnull
  public PreprocessorContext getContext() {
    return context;
  }

  public JCPreprocessor(@Nonnull final PreprocessorContext context) {
    assertNotNull("Configurator is null", context);
    this.context = context;
  }

  @Nonnull
  public PreprocessingStatistics execute() throws IOException {
    final long timeStart = System.currentTimeMillis();

    processCfgFiles();

    final File[] srcDirs = context.getSourceDirectoryAsFiles();
    final Collection<FileInfoContainer> filesToBePreprocessed = findAllFilesToBePreprocessed(srcDirs, context.getExcludedFolderPatterns());

    final List<PreprocessingState.ExcludeIfInfo> excludedIf = processGlobalDirectives(filesToBePreprocessed);

    processFileExclusion(excludedIf);
    if (!context.isFileOutputDisabled()) {
      createDestinationDirectory();
    }
    final PreprocessingStatistics stat = preprocessFiles(filesToBePreprocessed);

    final long elapsedTime = System.currentTimeMillis() - timeStart;
    this.context.logInfo("-----------------------------------------------------------------");
    this.context.logInfo("Completed, preprocessed " + stat.getNumberOfPreprocessed() + " files, copied " + stat.getNumberOfCopied() + " files, elapsed time " + elapsedTime + "ms");
    return stat;
  }

  private void processFileExclusion(@Nonnull @MustNotContainNull final List<PreprocessingState.ExcludeIfInfo> foundExcludeIf) {
    final String DIRECTIVE_NAME = new ExcludeIfDirectiveHandler().getFullName();

    for (final PreprocessingState.ExcludeIfInfo item : foundExcludeIf) {
      final String condition = item.getCondition();
      final File file = item.getFileInfoContainer().getSourceFile();

      Value val = null;

      if (context.isVerbose()) {
        context.logForVerbose("Processing condition '" + condition + "' for file '" + file.getAbsolutePath() + "'");
      }

      try {
        val = Expression.evalExpression(condition, context);
      }
      catch (IllegalArgumentException ex) {
        throw new PreprocessorException("Wrong expression at " + DIRECTIVE_NAME, condition, new FilePositionInfo[]{new FilePositionInfo(file, item.getStringIndex())}, ex);
      }

      if (val.getType() != ValueType.BOOLEAN) {
        throw new PreprocessorException("Expression at " + DIRECTIVE_NAME + " is not a boolean one", condition, new FilePositionInfo[]{new FilePositionInfo(file, item.getStringIndex())}, null);
      }

      if (val.asBoolean()) {
        item.getFileInfoContainer().setExcluded(true);
        if (context.isVerbose()) {
          context.logForVerbose("File '" + file.getAbsolutePath() + "' excluded because '" + condition + "' returns TRUE");
        }
      }
    }
  }

  @Nonnull
  @MustNotContainNull
  private List<PreprocessingState.ExcludeIfInfo> processGlobalDirectives(@Nonnull @MustNotContainNull final Collection<FileInfoContainer> files) throws IOException {
    final List<PreprocessingState.ExcludeIfInfo> result = new ArrayList<PreprocessingState.ExcludeIfInfo>();
    for (final FileInfoContainer fileRef : files) {
      if (!(fileRef.isExcludedFromPreprocessing() || fileRef.isForCopyOnly())) {
        final long startTime = System.currentTimeMillis();
        result.addAll(fileRef.processGlobalDirectives(null, context));
        final long elapsedTime = System.currentTimeMillis() - startTime;
        if (context.isVerbose()) {
          context.logForVerbose("Global search completed for file '" + PreprocessorUtils.getFilePath(fileRef.getSourceFile()) + "', elapsed time " + elapsedTime + "ms");
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
              context.logForVerbose("Ignore copying because exists with the same content : " + PreprocessorUtils.getFilePath(fileRef.getSourceFile()) + " -> {dst}" + fileRef.getDestinationFilePath());
            }
          }

          if (doCopy) {
            if (context.isVerbose()) {
              context.logForVerbose("Copy file " + PreprocessorUtils.getFilePath(fileRef.getSourceFile()) + " -> {dst}" + fileRef.getDestinationFilePath());
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
          context.logForVerbose("File preprocessing completed  '" + PreprocessorUtils.getFilePath(fileRef.getSourceFile()) + "', elapsed time " + elapsedTime + "ms");
        }
        prepFileCounter++;
      }
    }
    final PreprocessingStatistics stat = new PreprocessingStatistics(prepFileCounter, copFileCounter);
    return stat;
  }

  private void createDestinationDirectory() throws IOException {
    final File destination = context.getDestinationDirectoryAsFile();

    final boolean destinationExistsAndDirectory = destination.exists() && destination.isDirectory();

    if (context.doesClearDestinationDirBefore() && destinationExistsAndDirectory) {
      try {
        FileUtils.cleanDirectory(destination);
      }
      catch (IOException ex) {
        throw new IOException("I can't clean the destination directory [" + PreprocessorUtils.getFilePath(destination) + ']', ex);
      }
    }

    if (!destinationExistsAndDirectory && !destination.mkdirs()) {
      throw new IOException("I can't make the destination directory [" + PreprocessorUtils.getFilePath(destination) + ']');
    }
  }

  @Nonnull
  @MustNotContainNull
  private Collection<FileInfoContainer> findAllFilesToBePreprocessed(@Nonnull @MustNotContainNull final File[] srcDirs, @Nonnull @MustNotContainNull final String[] excludedFolderPatterns) throws IOException {
    final Collection<FileInfoContainer> result = new ArrayList<FileInfoContainer>();

    final AntPathMatcher antPathMatcher = new AntPathMatcher();

    for (final File dir : srcDirs) {
      String canonicalPathForSrcDirectory = dir.getCanonicalPath();
      
      if (!canonicalPathForSrcDirectory.endsWith(File.separator)) {
        canonicalPathForSrcDirectory += File.separator;
      }

      final Set<File> allFoundFiles = findAllFiles(canonicalPathForSrcDirectory, dir, antPathMatcher, excludedFolderPatterns);

      for (final File file : allFoundFiles) {
        if (context.isFileExcludedFromProcess(file)) {
          // ignore excluded file
          continue;
        }

        final String filePath = file.getCanonicalPath();
        final String relativePath = filePath.substring(canonicalPathForSrcDirectory.length());

        final FileInfoContainer reference = new FileInfoContainer(file, relativePath, !this.context.isFileAllowedToBeProcessed(file));
        result.add(reference);
      }

    }

    return result;
  }

  @Nonnull
  private Set<File> findAllFiles(@Nonnull final String baseFolderCanonicalPath, @Nonnull final File dir, @Nonnull final AntPathMatcher antPathMatcher, @Nonnull @MustNotContainNull final String[] excludedFolderPatterns) throws IOException {
    final Set<File> result = new HashSet<File>();
    final File[] allowedFiles = dir.listFiles();
    
    final String normalizedBasePath = FilenameUtils.normalize(baseFolderCanonicalPath, true);
    
    for (final File file : allowedFiles) {
      if (file.isDirectory()) {
        boolean process = true;

        final String folderPath = file.getCanonicalPath();

        String excludingPattern = null;

        if (excludedFolderPatterns.length != 0) {
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
          this.context.logForVerbose("Folder '" + folderPath + "' excluded for pattern '" + excludingPattern + "'");
        }
      } else {
        result.add(file);
      }
    }
    return result;
  }

  public static void main(@Nonnull @MustNotContainNull final String... args) {
    printHeader();

    final String[] normalizedStrings = PreprocessorUtils.replaceStringPrefix(new String[]{"--", "-"}, "/", PreprocessorUtils.replaceChar(args, '$', '\"'));

    final PreprocessorContext preprocessorContext;

    try {
      preprocessorContext = processCommandString(null, args, normalizedStrings);
    }
    catch (IOException ex) {
      System.err.println("Error during command line processing [" + ex.getMessage() + ']');
      System.exit(1);
      throw new RuntimeException("To show compiler executiion stop");
    }

    final JCPreprocessor preprocessor = new JCPreprocessor(preprocessorContext);

    try {
      preprocessor.execute();
    }
    catch (Exception unexpected) {
      System.err.println(PreprocessorException.referenceAsString(' ', unexpected));
      System.exit(1);
    }

    System.exit(0);
  }

  @Nonnull
  private static PreprocessorContext processCommandString(@Nullable final PreprocessorContext context, @Nonnull @MustNotContainNull final String[] originalStrings, @Nonnull @MustNotContainNull final String[] normalizedStrings) throws IOException {
    final PreprocessorContext result = context == null ? new PreprocessorContext() : context;

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
        System.err.println("Can't process a command line argument, may be some wrong usage : " + originalStrings[i]);
        System.out.println();
        System.out.println("Take a look at the CLI help below, please");
        help();
        System.exit(1);
      }
    }

    return result;
  }

  void processCfgFiles() throws IOException {

    for (final File file : context.getConfigFiles()) {
      final String[] wholeFile = PreprocessorUtils.readWholeTextFileIntoArray(file, "UTF-8", null);

      int readStringIndex = -1;
      for (final String curString : wholeFile) {
        final String trimmed = curString.trim();
        readStringIndex++;

        if (trimmed.isEmpty() || trimmed.charAt(0) == '#') {
          // do nothing
        } else if (trimmed.charAt(0) == '@') {
          PreprocessorUtils.throwPreprocessorException("You can't start any string in a global variable defining file with \'@\'", trimmed, file, readStringIndex, null);
        } else if (trimmed.charAt(0) == '/') {
          // a command line argument
          boolean processed = false;
          try {
            for (CommandLineHandler handler : getCommandLineHandlers()) {
              if (context.isVerbose()) {
                context.logForVerbose("Processing сonfig file key \'" + trimmed + "\' at " + file.getName() + ':' + (readStringIndex + 1));
              }
              if (handler.processCommandLineKey(trimmed, context)) {
                processed = true;
                break;
              }
            }
          }
          catch (Exception unexpected) {
            PreprocessorUtils.throwPreprocessorException("Exception during directive processing", trimmed, file, readStringIndex, unexpected);
          }

          if (!processed) {
            PreprocessorUtils.throwPreprocessorException("Unsupported or disallowed directive", trimmed, file, readStringIndex, null);
          }
        } else {
          // a global variable
          final String[] splitted = PreprocessorUtils.splitForEqualChar(trimmed);
          if (splitted.length != 2) {
            PreprocessorUtils.throwPreprocessorException("Wrong variable definition", trimmed, file, readStringIndex, null);
          }
          final String name = splitted[0].trim().toLowerCase(Locale.ENGLISH);
          final String expression = splitted[1].trim();
          if (name.isEmpty()) {
            PreprocessorUtils.throwPreprocessorException("Empty variable name detected", trimmed, file, readStringIndex, null);
          }

          try {
            final Value result = Expression.evalExpression(expression, context);
            context.setGlobalVariable(name, result);

            if (context.isVerbose()) {
              context.logForVerbose("Register global variable " + name + " = " + result.toString() + " (" + file.getName() + ':' + (readStringIndex + 1) + ')');
            }
          }
          catch (Exception unexpected) {
            PreprocessorUtils.throwPreprocessorException("Can't process the global variable definition", trimmed, file, readStringIndex, unexpected);
          }
        }
      }
    }
  }

  private static void printHeader() {
    System.out.println(InfoHelper.getProductName() + ' ' + InfoHelper.getVersion());
    System.out.println(InfoHelper.getSite());
    System.out.println(InfoHelper.getCopyright());
  }

  private static void help() {
    System.out.println();

    for (final String str : InfoHelper.makeTextForHelpInfo()) {
      System.out.println(str);
    }
  }
}
