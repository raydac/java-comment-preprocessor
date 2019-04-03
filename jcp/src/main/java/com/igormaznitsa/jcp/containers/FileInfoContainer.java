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

package com.igormaznitsa.jcp.containers;

import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.directives.AbstractDirectiveHandler;
import com.igormaznitsa.jcp.directives.AfterDirectiveProcessingBehaviour;
import com.igormaznitsa.jcp.directives.DirectiveArgumentType;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import com.igormaznitsa.jcp.utils.ResetablePrinter;
import com.igormaznitsa.meta.annotation.MustNotContainNull;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

/**
 * The class is one from the main classes in the preprocessor because it describes a preprocessing file and contains business logic for the process
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
@Data
public class FileInfoContainer {

  private static final Pattern DIRECTIVE_HASH_PREFIXED = Pattern.compile("^\\s*//\\s*#(.*)$");
  private static final Pattern DIRECTIVE_TWO_DOLLARS_PREFIXED = Pattern.compile("^\\s*//\\s*\\$\\$(.*)$");
  private static final Pattern DIRECTIVE_SINGLE_DOLLAR_PREFIXED = Pattern.compile("^\\s*//\\s*\\$(.*)$");
  private static final Pattern DIRECTIVE_TAIL_REMOVER = Pattern.compile("\\/\\*\\s*-\\s*\\*\\/");

  /**
   * The source file for the container
   */
  private final File sourceFile;

  /**
   * The flag shows that the file should be just copied into the destination place without any preprocessing
   */
  private final boolean copyOnly;

  /**
   * The flag shows that the file has been excluded from preprocessing and it will not be preprocessed and copied
   */
  private boolean excludedFromPreprocessing;

  /**
   * The destination directory for the file
   */
  private String targetFolder;

  /**
   * The destination name for the file
   */
  private String targetFileName;

  public FileInfoContainer(@Nonnull final File srcFile, @Nonnull final String targetFileName, final boolean copyOnly) {
    assertNotNull("Source file is null", srcFile);
    assertNotNull("Target file name is null", targetFileName);

    this.copyOnly = copyOnly;
    excludedFromPreprocessing = false;
    sourceFile = srcFile;

    int lastDirSeparator = targetFileName.lastIndexOf('/');
    if (lastDirSeparator < 0) {
      lastDirSeparator = targetFileName.lastIndexOf('\\');
    }

    if (lastDirSeparator < 0) {
      this.targetFolder = "." + File.separatorChar;
      this.targetFileName = targetFileName;
    } else {
      this.targetFolder = targetFileName.substring(0, lastDirSeparator);
      this.targetFileName = targetFileName.substring(lastDirSeparator + 1);
    }
  }

  @Nonnull
  private static String findTailRemover(@Nonnull final String str, @Nonnull final PreprocessorContext context) {
    String result = str;
    if (context.isAllowWhitespaces()) {
      final Matcher matcher = DIRECTIVE_TAIL_REMOVER.matcher(str);
      if (matcher.find()) {
        result = str.substring(0, matcher.start());
      }
    } else {
      final int tailRemoverStart = str.indexOf("/*-*/");
      if (tailRemoverStart >= 0) {
        result = str.substring(0, tailRemoverStart);
      }
    }
    return result;
  }

  public void setTargetFolder(@Nonnull final String targetFolder) {
    this.targetFolder = assertNotNull("Target folder must not be null", targetFolder);
  }

  public void setTargetName(@Nonnull final String targetName) {
    this.targetFileName = assertNotNull("Target file name must not be null", targetFileName);
  }

  @Nonnull
  public String makeTargetFilePathAsString() {
    String targetFolder = this.getTargetFolder();
    if (!targetFolder.isEmpty() && targetFolder.charAt(targetFolder.length() - 1) != File.separatorChar) {
      targetFolder = targetFolder + File.separatorChar;
    }

    return targetFolder + this.getTargetFileName();
  }

  @Override
  @Nonnull
  public String toString() {
    return String.format("%s: source=%s, targetFolder=%s, targetName=%s", this.getClass().getSimpleName(), PreprocessorUtils.getFilePath(this.getSourceFile()), this.getTargetFolder(), this.getTargetFileName());
  }

  @Nonnull
  @MustNotContainNull
  public List<PreprocessingState.ExcludeIfInfo> processGlobalDirectives(@Nullable final PreprocessingState state, @Nonnull final PreprocessorContext context) throws IOException {
    final PreprocessingState preprocessingState = state == null ? context.produceNewPreprocessingState(this, 0) : state;

    String leftTrimmedString = null;
    try {
      while (!Thread.currentThread().isInterrupted()) {
        String nonTrimmedProcessingString = preprocessingState.nextLine();

        final Set<PreprocessingFlag> processFlags = preprocessingState.getPreprocessingFlags();

        if (processFlags.contains(PreprocessingFlag.END_PROCESSING) || processFlags.contains(PreprocessingFlag.ABORT_PROCESSING)) {
          if (!processFlags.contains(PreprocessingFlag.ABORT_PROCESSING)) {
            processFlags.remove(PreprocessingFlag.END_PROCESSING);
          }
          nonTrimmedProcessingString = null;
        }

        if (nonTrimmedProcessingString == null) {
          preprocessingState.popTextContainer();
          if (preprocessingState.isIncludeStackEmpty()) {
            break;
          } else {
            continue;
          }
        }

        leftTrimmedString = PreprocessorUtils.leftTrim(nonTrimmedProcessingString);

        if (isHashPrefixed(leftTrimmedString, context)) {

          checkAndLogWarningIfNotAllowedWhitespaceSituation(context, preprocessingState, leftTrimmedString);

          switch (processDirective(preprocessingState, extractHashPrefixedDirective(leftTrimmedString, context), context, true)) {
            case PROCESSED:
            case READ_NEXT_LINE:
            case SHOULD_BE_COMMENTED:
              continue;
            default:
              throw new Error("Unsupported result");
          }
        }
      }
    } catch (Exception unexpected) {
      final PreprocessorException pp = PreprocessorException.extractPreprocessorException(unexpected);
      if (pp == null) {
        throw preprocessingState.makeException("Unexpected exception detected", leftTrimmedString, unexpected);
      } else {
        throw pp;
      }
    }

    if (!preprocessingState.isIfStackEmpty()) {
      final TextFileDataContainer lastIf = assertNotNull(preprocessingState.peekIf());
      throw new PreprocessorException("Unclosed " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + "_if instruction detected",
          "", new FilePositionInfo[] {new FilePositionInfo(lastIf.getFile(), lastIf.getNextStringIndex())}, null);
    }

    return preprocessingState.popAllExcludeIfInfoData();
  }

  private void checkAndLogWarningIfNotAllowedWhitespaceSituation(@Nonnull final PreprocessorContext context, @Nonnull final PreprocessingState state, @Nonnull final String leftTrimmedHashPrefixedString) {
    if (!leftTrimmedHashPrefixedString.startsWith("//#") && !context.isAllowWhitespaces()) {
      final TextFileDataContainer textContainer = state.getCurrentIncludeFileContainer();
      String lineInfo = "<NONE>";
      if (textContainer != null) {
        lineInfo = String.format("%s:%d)",textContainer.getFile().getAbsolutePath(), textContainer.getNextStringIndex());
      }
      context.logWarning("Detected line with hash prefixed by whitespace but whitespace is not allowed and directive will be ignored: " + lineInfo);
    }
  }

  private boolean isDoubleDollarPrefixed(@Nonnull final String line, @Nonnull final PreprocessorContext context) {
    if (context.isAllowWhitespaces()) {
      return DIRECTIVE_TWO_DOLLARS_PREFIXED.matcher(line).matches();
    } else {
      return line.startsWith("//$$");
    }
  }

  private boolean isSingleDollarPrefixed(@Nonnull final String line, @Nonnull final PreprocessorContext context) {
    if (context.isAllowWhitespaces()) {
      return DIRECTIVE_SINGLE_DOLLAR_PREFIXED.matcher(line).matches();
    } else {
      return line.startsWith("//$");
    }
  }

  private boolean isHashPrefixed(@Nonnull final String line, @Nonnull final PreprocessorContext context) {
    if (context.isAllowWhitespaces()) {
      return DIRECTIVE_HASH_PREFIXED.matcher(line).matches();
    } else {
      return line.startsWith(AbstractDirectiveHandler.DIRECTIVE_PREFIX);
    }
  }

  @Nonnull
  private String extractHashPrefixedDirective(@Nonnull final String line, @Nonnull final PreprocessorContext context) {
    if (context.isAllowWhitespaces()) {
      final Matcher matcher = DIRECTIVE_HASH_PREFIXED.matcher(line);
      if (matcher.find()) {
        return matcher.group(1);
      } else {
        throw new Error("Unexpected situation, directive is not found, contact developer! (" + line + ')');
      }
    } else {
      return PreprocessorUtils.extractTail(AbstractDirectiveHandler.DIRECTIVE_PREFIX, line);
    }
  }

  @Nonnull
  private String extractDoubleDollarPrefixedDirective(@Nonnull final String line, @Nonnull final PreprocessorContext context) {
    String tail;
    if (context.isAllowWhitespaces()) {
      final Matcher matcher = DIRECTIVE_TWO_DOLLARS_PREFIXED.matcher(line);
      if (matcher.find()) {
        tail = matcher.group(1);
      } else {
        throw new Error("Unexpected situation, '//$$' directive is not found, contact developer! (" + line + ')');
      }
    } else {
      tail = PreprocessorUtils.extractTail("//$$", line);
    }

    if (context.isPreserveIndents()) {
      tail = PreprocessorUtils.replacePartByChar(line, ' ', 0, line.length() - tail.length());
    }
    return tail;
  }

  @Nonnull
  private String extractSingleDollarPrefixedDirective(@Nonnull final String line, @Nonnull final PreprocessorContext context) {
    String tail;
    if (context.isAllowWhitespaces()) {
      final Matcher matcher = DIRECTIVE_SINGLE_DOLLAR_PREFIXED.matcher(line);
      if (matcher.find()) {
        tail = matcher.group(1);
      } else {
        throw new Error("Unexpected situation, '//$' directive is not found, contact developer! (" + line + ')');
      }
    } else {
      tail = PreprocessorUtils.extractTail("//$", line);
    }

    if (context.isPreserveIndents()) {
      tail = PreprocessorUtils.replacePartByChar(line, ' ', 0, line.length() - tail.length());
    }
    return tail;
  }

  /**
   * Preprocess file, NB! it doesn't clear local variables automatically for cloned contexts
   *
   * @param state   the start preprocessing state, can be null
   * @param context the preprocessor context, must not be null
   * @return the state for the preprocessed file
   * @throws IOException           it will be thrown for IO errors
   * @throws PreprocessorException it will be thrown for violation of preprocessing logic, like undefined variable
   */
  @Nonnull
  public PreprocessingState preprocessFile(@Nullable final PreprocessingState state, @Nonnull final PreprocessorContext context) throws IOException {
    // do not clear local variables for cloned context to keep them in the new context
    if (!context.isCloned()) {
      context.clearLocalVariables();
    }

    final PreprocessingState preprocessingState = state != null ? state : context.produceNewPreprocessingState(this, 1);

    String leftTrimmedString = null;

    TextFileDataContainer lastTextFileDataContainer = null;

    try {
      while (!Thread.currentThread().isInterrupted()) {
        String rawString = preprocessingState.nextLine();
        final boolean presentedNextLine = preprocessingState.hasReadLineNextLineInEnd();

        final Set<PreprocessingFlag> processFlags = preprocessingState.getPreprocessingFlags();

        if (processFlags.contains(PreprocessingFlag.END_PROCESSING) || processFlags.contains(PreprocessingFlag.ABORT_PROCESSING)) {
          if (!processFlags.contains(PreprocessingFlag.ABORT_PROCESSING)) {
            processFlags.remove(PreprocessingFlag.END_PROCESSING);
          }
          rawString = null;
        }

        if (preprocessingState.getPreprocessingFlags().contains(PreprocessingFlag.END_PROCESSING)) {
          preprocessingState.getPreprocessingFlags().remove(PreprocessingFlag.END_PROCESSING);
          rawString = null;
        }

        if (rawString == null) {
          lastTextFileDataContainer = preprocessingState.popTextContainer();
          if (preprocessingState.isIncludeStackEmpty()) {
            break;
          } else {
            continue;
          }
        }

        leftTrimmedString = PreprocessorUtils.leftTrim(rawString);

        final String stringPrefix;
        if (leftTrimmedString.isEmpty()) {
          stringPrefix = rawString;
        } else {
          final int numberOfSpacesAtTheLineBeginning = rawString.indexOf(leftTrimmedString);

          if (numberOfSpacesAtTheLineBeginning > 0) {
            stringPrefix = rawString.substring(0, numberOfSpacesAtTheLineBeginning);
          } else {
            stringPrefix = "";
          }
        }

        String stringToBeProcessed = leftTrimmedString;

        final boolean doPrintLn = presentedNextLine || !context.isCareForLastEol();

        if (isHashPrefixed(stringToBeProcessed, context)) {
          checkAndLogWarningIfNotAllowedWhitespaceSituation(context, preprocessingState, stringToBeProcessed);

          final String extractedDirective = extractHashPrefixedDirective(stringToBeProcessed, context);
          switch (processDirective(preprocessingState, extractedDirective, context, false)) {
            case PROCESSED:
            case READ_NEXT_LINE: {
              if (context.isKeepLines()) {
                final String text = stringPrefix + AbstractDirectiveHandler.PREFIX_FOR_KEEPING_LINES_PROCESSED_DIRECTIVES + extractedDirective;
                final ResetablePrinter thePrinter = assertNotNull(preprocessingState.getPrinter());
                if (doPrintLn) {
                  thePrinter.println(text, context.getEol());
                } else {
                  thePrinter.print(text);
                }
              }
              continue;
            }
            case SHOULD_BE_COMMENTED: {
              final String text = stringPrefix + AbstractDirectiveHandler.PREFIX_FOR_KEEPING_LINES_PROCESSED_DIRECTIVES + extractedDirective;
              final ResetablePrinter thePrinter = assertNotNull(preprocessingState.getPrinter());
              if (doPrintLn) {
                thePrinter.println(text, context.getEol());
              } else {
                thePrinter.print(text);
              }
              continue;
            }
            default:
              throw new Error("Unsupported result");
          }
        }

        final ResetablePrinter thePrinter = assertNotNull(preprocessingState.getPrinter());
        if (preprocessingState.isDirectiveCanBeProcessed() && !preprocessingState.getPreprocessingFlags().contains(PreprocessingFlag.TEXT_OUTPUT_DISABLED)) {
          final boolean startsWithTwoDollars = isDoubleDollarPrefixed(leftTrimmedString, context);

          if (!startsWithTwoDollars) {
            stringToBeProcessed = PreprocessorUtils.processMacroses(leftTrimmedString, context);
          }

          if (startsWithTwoDollars) {
            // Output the tail of the string to the output stream without comments and macroses
            thePrinter.print(stringPrefix);
            final String text = extractDoubleDollarPrefixedDirective(leftTrimmedString, context);
            if (doPrintLn) {
              thePrinter.println(text, context.getEol());
            } else {
              thePrinter.print(text);
            }
          } else if (isSingleDollarPrefixed(stringToBeProcessed, context)) {
            // Output the tail of the string to the output stream without comments
            thePrinter.print(stringPrefix);

            final String text = extractSingleDollarPrefixedDirective(stringToBeProcessed, context);

            if (doPrintLn) {
              thePrinter.println(text, context.getEol());
            } else {
              thePrinter.print(text);
            }
          } else {
            // Just string
            final String strToOut = findTailRemover(stringToBeProcessed, context);

            if (preprocessingState.getPreprocessingFlags().contains(PreprocessingFlag.COMMENT_NEXT_LINE)) {
              thePrinter.print(AbstractDirectiveHandler.ONE_LINE_COMMENT);
              preprocessingState.getPreprocessingFlags().remove(PreprocessingFlag.COMMENT_NEXT_LINE);
            }

            thePrinter.print(stringPrefix);
            if (doPrintLn) {
              thePrinter.println(strToOut, context.getEol());
            } else {
              thePrinter.print(strToOut);
            }
          }
        } else if (context.isKeepLines()) {
          final String text = AbstractDirectiveHandler.PREFIX_FOR_KEEPING_LINES + rawString;
          if (doPrintLn) {
            thePrinter.println(text, context.getEol());
          } else {
            thePrinter.print(text);
          }
        }
      }
    } catch (Exception unexpected) {
      final String message = unexpected.getMessage() == null ? "Unexpected exception" : unexpected.getMessage();
      throw preprocessingState.makeException(message, leftTrimmedString, unexpected);
    }

    if (!preprocessingState.isIfStackEmpty()) {
      final TextFileDataContainer lastIf = assertNotNull("'IF' stack is empty", preprocessingState.peekIf());
      throw new PreprocessorException("Unclosed " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + "if instruction detected",
          "", new FilePositionInfo[] {new FilePositionInfo(lastIf.getFile(), lastIf.getNextStringIndex())}, null);
    }
    if (!preprocessingState.isWhileStackEmpty()) {
      final TextFileDataContainer lastWhile = assertNotNull("'WHILE' stack is empty", preprocessingState.peekWhile());
      throw new PreprocessorException("Unclosed " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + "while instruction detected",
          "", new FilePositionInfo[] {new FilePositionInfo(lastWhile.getFile(), lastWhile.getNextStringIndex())}, null);
    }

    if (!context.isDryRun() && assertNotNull(lastTextFileDataContainer).isAutoFlush()) {
      final File outFile = context.createDestinationFileForPath(makeTargetFilePathAsString());

      final boolean wasSaved = preprocessingState.saveBuffersToFile(outFile, context.isKeepComments());

      if (context.isVerbose()) {
        context.logForVerbose(String.format("Content was %s into file '%s'", (wasSaved ? "saved" : "not saved"), outFile.toString()));
      }

      if (this.sourceFile != null && context.isKeepAttributes() && !PreprocessorUtils.copyFileAttributes(this.getSourceFile(), outFile)) {
        throw new IOException("Can't copy attributes in result file: " + outFile);
      }
    }
    return preprocessingState;
  }

  private boolean checkDirectiveArgumentRoughly(@Nonnull final AbstractDirectiveHandler directive, @Nonnull final String rest) {
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
        } else {
          final char firstChar = rest.charAt(0);
          result = firstChar == '+' || firstChar == '-';
          if (rest.length() > 1) {
            result = result && Character.isSpaceChar(rest.charAt(1));
          }
        }
      }
      break;
      case TAIL: {
        result = true;
      }
      break;
      default: {
        result = !trimmedRest.isEmpty() && Character.isSpaceChar(rest.charAt(0));
      }
      break;
    }

    return result;
  }

  @Nonnull
  protected AfterDirectiveProcessingBehaviour processDirective(@Nonnull final PreprocessingState state, @Nonnull final String directiveString, @Nonnull final PreprocessorContext context, final boolean firstPass) throws IOException {
    final boolean executionEnabled = state.isDirectiveCanBeProcessed();

    for (final AbstractDirectiveHandler handler : AbstractDirectiveHandler.getAllDirectives()) {
      final String name = handler.getName();
      if (directiveString.startsWith(name)) {
        if ((firstPass && !handler.isGlobalPhaseAllowed()) || (!firstPass && !handler.isPreprocessingPhaseAllowed())) {
          return AfterDirectiveProcessingBehaviour.READ_NEXT_LINE;
        }

        final boolean allowedForExecution = executionEnabled || !handler.executeOnlyWhenExecutionAllowed();

        final String restOfString = PreprocessorUtils.extractTail(name, directiveString);
        if (checkDirectiveArgumentRoughly(handler, restOfString)) {
          if (allowedForExecution) {
            return handler.execute(restOfString, context);
          } else {
            return context.isKeepLines() ? AfterDirectiveProcessingBehaviour.SHOULD_BE_COMMENTED : AfterDirectiveProcessingBehaviour.PROCESSED;
          }
        } else {
          throw context.makeException("Detected bad argument for " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + handler.getName(), null);
        }
      }
    }
    throw context.makeException("Unknown preprocessor directive [" + directiveString + ']', null);
  }

  public void setExcluded(final boolean flag) {
    excludedFromPreprocessing = flag;
  }
}
