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
package com.igormaznitsa.jcp.containers;

import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.directives.AbstractDirectiveHandler;
import com.igormaznitsa.jcp.directives.AfterDirectiveProcessingBehaviour;
import com.igormaznitsa.jcp.directives.DirectiveArgumentType;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The class is one from the main classes in the preprocessor because it
 * describes a preprocessing file and contains business logic for the process
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class FileInfoContainer {

  /**
   * The source file for the container
   */
  private final File sourceFile;

  /**
   * The flag shows that the file should be just copied into the destination
   * place without any preprocessing
   */
  private final boolean forCopyOnly;

  /**
   * The flag shows that the file has been excluded from preprocessing and it
   * will not be preprocessed and copied
   */
  private boolean excludedFromPreprocessing;

  /**
   * The destination directory for the file
   */
  private String destinationDir;

  /**
   * The destination name for the file
   */
  private String destinationName;

  public File getSourceFile() {
    return sourceFile;
  }

  public boolean isExcludedFromPreprocessing() {
    return excludedFromPreprocessing;
  }

  public boolean isForCopyOnly() {
    return forCopyOnly;
  }

  public String getDestinationDir() {
    return destinationDir;
  }

  public String getDestinationName() {
    return destinationName;
  }

  public FileInfoContainer(final File srcFile, final String dstFileName, final boolean copyOnly) {
    PreprocessorUtils.assertNotNull("The source file is null", srcFile);
    PreprocessorUtils.assertNotNull("The destination file name is null", dstFileName);

    forCopyOnly = copyOnly;
    excludedFromPreprocessing = false;
    sourceFile = srcFile;

    int dirSeparator = dstFileName.lastIndexOf('/');
    if (dirSeparator < 0) {
      dirSeparator = dstFileName.lastIndexOf('\\');
    }

    if (dirSeparator < 0) {
      destinationDir = "." + File.separatorChar;
      destinationName = dstFileName;
    }
    else {
      destinationDir = dstFileName.substring(0, dirSeparator);
      destinationName = dstFileName.substring(dirSeparator);
    }
  }

  public String getDestinationFilePath() {
    return destinationDir + File.separatorChar + destinationName;
  }

  @Override
  public String toString() {
    return "FileInfoContainer: file=" + PreprocessorUtils.getFilePath(sourceFile) + " toDir=" + destinationDir + " toName=" + destinationName;
  }

  public List<PreprocessingState.ExcludeIfInfo> processGlobalDirectives(final PreprocessingState state, final PreprocessorContext context) throws PreprocessorException, IOException {
    final PreprocessingState preprocessingState = state == null ? context.produceNewPreprocessingState(this) : state;

    String trimmedProcessingString = null;
    try {
      while (true) {
        String nonTrimmedProcessingString = preprocessingState.nextLine();
        if (preprocessingState.getPreprocessingFlags().contains(PreprocessingFlag.END_PROCESSING)) {
          nonTrimmedProcessingString = null;
        }

        if (nonTrimmedProcessingString == null) {
          if (!preprocessingState.isOnlyRootOnStack()) {
            preprocessingState.popTextContainer();
            continue;
          }
          else {
            break;
          }
        }

        trimmedProcessingString = nonTrimmedProcessingString.trim();

        if (trimmedProcessingString.startsWith(AbstractDirectiveHandler.DIRECTIVE_PREFIX)) {
          switch (processDirective(preprocessingState, PreprocessorUtils.extractTail(AbstractDirectiveHandler.DIRECTIVE_PREFIX, trimmedProcessingString), context, true)) {
            case PROCESSED:
            case READ_NEXT_LINE:
            case SHOULD_BE_COMMENTED:
              continue;
            default:
              throw new Error("Unsupported result");
          }
        }
      }
    }
    catch (Exception unexpected) {
      final PreprocessorException pp = PreprocessorException.extractPreprocessorException(unexpected);
      if (pp == null) {
        throw preprocessingState.makeException("Unexpected exception detected", trimmedProcessingString, unexpected);
      }
      else {
        throw pp;
      }
    }

    if (!preprocessingState.isIfStackEmpty()) {
      final TextFileDataContainer lastIf = preprocessingState.peekIf();
      throw new PreprocessorException("Unclosed " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + "_if instruction detected",
              "", new FilePositionInfo[]{new FilePositionInfo(lastIf.getFile(), lastIf.getNextStringIndex())}, null);
    }

    return preprocessingState.popAllExcludeIfInfoData();
  }

  /**
   * Preprocess file, NB! it doesn't clear local variables automatically for
   * cloned contexts
   *
   * @param state the start preprocessing state, can be null
   * @param context the preprocessor context, must not be null
   * @return the state for the preprocessed file
   * @throws IOException
   * @throws PreprocessorException
   */
  public PreprocessingState preprocessFile(final PreprocessingState state, final PreprocessorContext context) throws IOException, PreprocessorException {
    // do not clear local variables for cloned context to keep them in the new context
    if (!context.isCloned()) {
      context.clearLocalVariables();
    }

    final PreprocessingState preprocessingState = state != null ? state : context.produceNewPreprocessingState(this);

    String trimmedProcessingString = null;
    try {
      while (true) {
        String nonTrimmedProcessingString = preprocessingState.nextLine();
        if (preprocessingState.getPreprocessingFlags().contains(PreprocessingFlag.END_PROCESSING)) {
          nonTrimmedProcessingString = null;
        }

        if (nonTrimmedProcessingString == null) {
          if (!preprocessingState.isOnlyRootOnStack()) {
            preprocessingState.popTextContainer();
            continue;
          }
          else {
            break;
          }
        }

        trimmedProcessingString = nonTrimmedProcessingString.trim();

        final String stringPrefix;
        if (trimmedProcessingString.isEmpty()) {
          stringPrefix = nonTrimmedProcessingString;
        }
        else {
          final int numberOfSpacesAtTheLineBeginning = nonTrimmedProcessingString.indexOf(trimmedProcessingString);

          if (numberOfSpacesAtTheLineBeginning > 0) {
            stringPrefix = nonTrimmedProcessingString.substring(0, numberOfSpacesAtTheLineBeginning);
          }
          else {
            stringPrefix = "";
          }
        }

        String stringToBeProcessed = trimmedProcessingString;

        if (stringToBeProcessed.startsWith(AbstractDirectiveHandler.DIRECTIVE_PREFIX)) {
          final String extractedDirective = PreprocessorUtils.extractTail(AbstractDirectiveHandler.DIRECTIVE_PREFIX, stringToBeProcessed);
          switch (processDirective(preprocessingState, extractedDirective, context, false)) {
            case PROCESSED:
            case READ_NEXT_LINE: {
              if (context.isKeepLines()) {
                preprocessingState.getPrinter().println(stringPrefix + AbstractDirectiveHandler.PREFIX_FOR_KEEPING_LINES_PROCESSED_DIRECTIVES + extractedDirective);
              }
              continue;
            }
            case SHOULD_BE_COMMENTED: {
              preprocessingState.getPrinter().println(stringPrefix + AbstractDirectiveHandler.PREFIX_FOR_KEEPING_LINES_PROCESSED_DIRECTIVES + extractedDirective);
              continue;
            }
            default:
              throw new Error("Unsupported result");
          }
        }

        if (preprocessingState.isDirectiveCanBeProcessed() && !preprocessingState.getPreprocessingFlags().contains(PreprocessingFlag.TEXT_OUTPUT_DISABLED)) {
          final boolean startsWithTwoDollars = trimmedProcessingString.startsWith("//$$");

          if (!startsWithTwoDollars) {
            stringToBeProcessed = PreprocessorUtils.processMacroses(trimmedProcessingString, context);
          }

          if (startsWithTwoDollars) {
            // Output the tail of the string to the output stream without comments and macroses
            preprocessingState.getPrinter().print(stringPrefix);
            preprocessingState.getPrinter().println(PreprocessorUtils.extractTail("//$$", trimmedProcessingString));
          }
          else if (stringToBeProcessed.startsWith("//$")) {
            // Output the tail of the string to the output stream without comments
            preprocessingState.getPrinter().print(stringPrefix);
            preprocessingState.getPrinter().println(PreprocessorUtils.extractTail("//$", stringToBeProcessed));
          }
          else {
            // Just string
            final String strToOut = processStringForTailRemover(stringToBeProcessed);

            if (preprocessingState.getPreprocessingFlags().contains(PreprocessingFlag.COMMENT_NEXT_LINE)) {
              preprocessingState.getPrinter().print(AbstractDirectiveHandler.ONE_LINE_COMMENT);
              preprocessingState.getPreprocessingFlags().remove(PreprocessingFlag.COMMENT_NEXT_LINE);
            }

            preprocessingState.getPrinter().print(stringPrefix);
            preprocessingState.getPrinter().println(strToOut);
          }
        }
        else if (context.isKeepLines()) {
          preprocessingState.getPrinter().println(AbstractDirectiveHandler.PREFIX_FOR_KEEPING_LINES + nonTrimmedProcessingString);
        }
      }
    }
    catch (Exception unexpected) {
      final String message = unexpected.getMessage() == null ? "Unexpected exception" : unexpected.getMessage();
      throw preprocessingState.makeException(message, trimmedProcessingString, unexpected);
    }

    if (!preprocessingState.isIfStackEmpty()) {
      final TextFileDataContainer lastIf = preprocessingState.peekIf();
      throw new PreprocessorException("Unclosed " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + "if instruction detected",
              "", new FilePositionInfo[]{new FilePositionInfo(lastIf.getFile(), lastIf.getNextStringIndex())}, null);
    }
    if (!preprocessingState.isWhileStackEmpty()) {
      final TextFileDataContainer lastWhile = preprocessingState.peekWhile();
      throw new PreprocessorException("Unclosed " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + "while instruction detected",
              "", new FilePositionInfo[]{new FilePositionInfo(lastWhile.getFile(), lastWhile.getNextStringIndex())}, null);
    }

    if (!context.isFileOutputDisabled()) {
      final File outFile = context.createDestinationFileForPath(getDestinationFilePath());
      preprocessingState.saveBuffersToFile(outFile, context.isRemoveComments());
    }
    return preprocessingState;
  }

  private static String processStringForTailRemover(final String str) {
    final int tailRemoverStart = str.indexOf("/*-*/");
    if (tailRemoverStart >= 0) {
      return str.substring(0, tailRemoverStart);
    }
    return str;
  }

  private boolean checkDirectiveArgumentRoughly(final AbstractDirectiveHandler directive, final String rest) {
    final DirectiveArgumentType argument = directive.getArgumentType();

    boolean result;
    final String trimmedRest = rest.trim();

    switch (argument) {
      case NONE: {
        result = trimmedRest.isEmpty();
      }
      break;
      case ONOFF: {
        if (trimmedRest.isEmpty()) {
          result = false;
        }
        else {
          final char firstChar = rest.charAt(0);
          result = firstChar == '+' || firstChar == '-';
          if (rest.length() > 1) {
            result = result && Character.isSpaceChar(rest.charAt(1));
          }
        }
      }
      break;
      default: {
        result = !trimmedRest.isEmpty() && Character.isSpaceChar(rest.charAt(0));
      }
      break;
    }

    return result;
  }

  protected AfterDirectiveProcessingBehaviour processDirective(final PreprocessingState state, final String trimmedString, final PreprocessorContext context, final boolean firstPass) throws IOException {
    final boolean executionEnabled = state.isDirectiveCanBeProcessed();

    for (final AbstractDirectiveHandler handler : AbstractDirectiveHandler.DIRECTIVES) {
      final String name = handler.getName();
      if (trimmedString.startsWith(name)) {
        if ((firstPass && !handler.isGlobalPhaseAllowed()) || (!firstPass && !handler.isPreprocessingPhaseAllowed())) {
          return AfterDirectiveProcessingBehaviour.READ_NEXT_LINE;
        }

        final boolean allowedForExecution = executionEnabled || !handler.executeOnlyWhenExecutionAllowed();

        final String restOfString = PreprocessorUtils.extractTail(name, trimmedString);
        if (checkDirectiveArgumentRoughly(handler, restOfString)) {
          if (allowedForExecution) {
            return handler.execute(restOfString.trim(), context);
          }
          else {
            return context.isKeepLines() ? AfterDirectiveProcessingBehaviour.SHOULD_BE_COMMENTED : AfterDirectiveProcessingBehaviour.PROCESSED;
          }
        }
        else {
          final String text = "Directive " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + handler.getName() + " has wrong argument";
          throw new IllegalArgumentException(text, context.makeException(text, null));
        }
      }
    }
    final String text = "Unknown preprocessor directive detected [" + trimmedString + ']';
    throw new IllegalArgumentException(text, context.makeException(text, null));
  }

  public void setDestinationDir(final String destDir) {
    PreprocessorUtils.assertNotNull("String is null",destDir);
    destinationDir = destDir;
  }

  public void setDestinationName(final String destName) {
    PreprocessorUtils.assertNotNull("String is null", destName);
    destinationName = destName;
  }

  public void setExcluded(final boolean flag) {
    excludedFromPreprocessing = flag;
  }
}
