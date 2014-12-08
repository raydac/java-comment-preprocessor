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
import org.apache.commons.io.FileUtils;

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
    
    public int getNumberOfCopied(){
      return this.numberOfCopied;
    }
    
    public int getNumberOfPreprocessed(){
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
    new RemoveCommentsHandler(),
    new KeepLineHandler(),
    new VerboseHandler(),
    new GlobalVariableDefiningFileHandler(),
    new GlobalVariableHandler(),
    new CareForLastNextLineCharHandler()
  };

  public static Iterable<CommandLineHandler> getCommandLineHandlers() {
    return Arrays.asList(COMMAND_LINE_HANDLERS);
  }

  public PreprocessorContext getContext() {
    return context;
  }

  public JCPreprocessor(final PreprocessorContext context) {
    PreprocessorUtils.assertNotNull("Configurator is null", context);
    this.context = context;
  }

  public PreprocessingStatistics execute() throws IOException {
    final long timeStart = System.currentTimeMillis();
    
    processCfgFiles();

    final File[] srcDirs = context.getSourceDirectoryAsFiles();
    final Collection<FileInfoContainer> filesToBePreprocessed = findAllFilesToBePreprocessed(srcDirs);
    
    final List<PreprocessingState.ExcludeIfInfo> excludedIf = processGlobalDirectives(filesToBePreprocessed);

    processFileExclusion(excludedIf);
    if (!context.isFileOutputDisabled()) {
      createDestinationDirectory();
    }
    final PreprocessingStatistics stat = preprocessFiles(filesToBePreprocessed);
    
    final long elapsedTime = System.currentTimeMillis()-timeStart;
    this.context.logInfo("-----------------------------------------------------------------");
    this.context.logInfo("Completed, preprocessed "+stat.getNumberOfPreprocessed()+" files, copied "+stat.getNumberOfCopied()+" files, elapsed time "+elapsedTime+"ms");
    return stat;
  }

  private void processFileExclusion(final List<PreprocessingState.ExcludeIfInfo> foundExcludeIf) {
    final String DIRECTIVE_NAME = new ExcludeIfDirectiveHandler().getFullName();

    for (final PreprocessingState.ExcludeIfInfo item : foundExcludeIf) {
      final String condition = item.getCondition();
      final File file = item.getFileInfoContainer().getSourceFile();

      Value val = null;

      if (context.isVerbose()){
        context.logForVerbose("Processing condition '"+condition+"' for file '"+file.getAbsolutePath()+"'");
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
          context.logForVerbose("File '" + file.getAbsolutePath() + "' excluded because '"+condition+"' returns TRUE");
        }
      }
    }
  }

  private List<PreprocessingState.ExcludeIfInfo> processGlobalDirectives(final Collection<FileInfoContainer> files) throws IOException {
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

  private PreprocessingStatistics preprocessFiles(final Collection<FileInfoContainer> files) throws IOException {
    int prepFileCounter = 0;
    int copFileCounter = 0;
    for (final FileInfoContainer fileRef : files) {
      if (fileRef.isExcludedFromPreprocessing()) {
        // do nothing
      }
      else if (fileRef.isForCopyOnly()) {
        if (!context.isFileOutputDisabled()) {
          if (context.isVerbose()){
            context.logForVerbose("Copy file "+PreprocessorUtils.getFilePath(fileRef.getSourceFile())+" -> {dst}"+fileRef.getDestinationFilePath());
          }
          PreprocessorUtils.copyFile(fileRef.getSourceFile(), context.createDestinationFileForPath(fileRef.getDestinationFilePath()));
          copFileCounter ++;
        }
      }
      else {
        final long startTime = System.currentTimeMillis();
        fileRef.preprocessFile(null, context);
        final long elapsedTime = System.currentTimeMillis() - startTime;
        if (context.isVerbose()){
          context.logForVerbose("File preprocessing completed  '"+PreprocessorUtils.getFilePath(fileRef.getSourceFile())+"', elapsed time "+elapsedTime+"ms");
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

    if (context.doesClearDestinationDirBefore()) {
      if (destinationExistsAndDirectory) {
        try{
          FileUtils.cleanDirectory(destination);
        }catch(IOException ex){
          throw new IOException("I can't clean the destination directory [" + PreprocessorUtils.getFilePath(destination) + ']',ex);
        }
      }
    }
    if (!destinationExistsAndDirectory) {
      if (!destination.mkdirs()) {
        throw new IOException("I can't make the destination directory [" + PreprocessorUtils.getFilePath(destination) + ']');
      }
    }
  }

  private Collection<FileInfoContainer> findAllFilesToBePreprocessed(final File[] srcDirs) throws IOException {
    final Collection<FileInfoContainer> result = new ArrayList<FileInfoContainer>();

    for (final File dir : srcDirs) {
      final String canonicalPathForSrcDirectory = dir.getCanonicalPath();
      final Set<File> allFoundFiles = findAllFiles(dir);

      for (final File file : allFoundFiles) {
        if (context.isFileExcludedFromProcess(file)) {
          // ignore excluded file
          continue;
        }

        final String filePath = file.getCanonicalPath();
        final String relativePath = filePath.substring(canonicalPathForSrcDirectory.length());

        final FileInfoContainer reference = new FileInfoContainer(file, relativePath, !context.isFileAllowedToBeProcessed(file));
        result.add(reference);
      }

    }

    return result;
  }

  private Set<File> findAllFiles(final File dir) {
    final Set<File> result = new HashSet<File>();
    final File[] allowedFiles = dir.listFiles();
    for (final File file : allowedFiles) {
      if (file.isDirectory()) {
        result.addAll(findAllFiles(file));
      }
      else {
        result.add(file);
      }
    }
    return result;
  }

  public static void main(final String... args) {
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
      System.err.println(PreprocessorException.referenceAsString(' ',unexpected));
      System.exit(1);
    }

    System.exit(0);
  }

  private static PreprocessorContext processCommandString(final PreprocessorContext context, final String[] originalStrings, final String[] normalizedStrings) throws IOException {
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
        }
        else if (trimmed.charAt(0) == '@') {
          PreprocessorUtils.throwPreprocessorException("You can't start any string in a global variable defining file with \'@\'", trimmed, file, readStringIndex, null);
        }
        else if (trimmed.charAt(0) == '/') {
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
        }
        else {
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
