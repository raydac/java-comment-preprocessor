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

import static java.util.Objects.requireNonNull;

import com.igormaznitsa.jcp.context.CommentTextProcessor;
import com.igormaznitsa.jcp.context.PreprocessingState;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.directives.AbstractDirectiveHandler;
import com.igormaznitsa.jcp.directives.AfterDirectiveProcessingBehaviour;
import com.igormaznitsa.jcp.directives.DirectiveArgumentType;
import com.igormaznitsa.jcp.exceptions.FilePositionInfo;
import com.igormaznitsa.jcp.exceptions.PreprocessorException;
import com.igormaznitsa.jcp.utils.PreprocessorUtils;
import com.igormaznitsa.jcp.utils.ResetablePrinter;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;

/**
 * The class is one from the main classes in the preprocessor because it describes a preprocessing file and contains business logic for the process
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
@Data
public class FileInfoContainer {

  public static final String WARNING_SPACE_BEFORE_HASH =
      "Detected hash prefixed comment line with whitespace, directive may be lost: ";
  private static final Pattern DIRECTIVE_HASH_PREFIXED = Pattern.compile("^\\s*//\\s*#(.*)$");
  private static final Pattern DIRECTIVE_TWO_DOLLARS_PREFIXED =
      Pattern.compile("^\\s*//\\s*\\$\\$(.*)$");
  private static final Pattern DIRECTIVE_TWO_DOLLARS_BLOCK_PREFIXED =
      Pattern.compile("^\\s*//\\s*\\$\\$\"\"\"(.*)$");
  private static final Pattern DIRECTIVE_SINGLE_DOLLAR_PREFIXED =
      Pattern.compile("^\\s*//\\s*\\$(.*)$");
  private static final Pattern DIRECTIVE_SINGLE_DOLLAR_BLOCK_PREFIXED =
      Pattern.compile("^\\s*//\\s*\\$\"(.*)$");
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
   * Collection of files generated with the file.
   */
  private final Collection<File> generatedResources = new HashSet<>();
  /**
   * Collection of files which took part during preprocessing of the file
   */
  private final Collection<File> includedSources = new HashSet<>();
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

  public FileInfoContainer(final File srcFile, final String targetFileName,
                           final boolean copyOnly) {
    requireNonNull(srcFile, "Source file is null");
    requireNonNull(targetFileName, "Target file name is null");

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

  private static String findTailRemover(final String str, final PreprocessorContext context) {
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

  /**
   * Check that text line starts with two commented dollar chars
   *
   * @param line               text line to be examined, must not be null
   * @param allowedWhitespaces if true then whitespaces allowed after line comment
   * @return true if the line starts with two commented dollar chars, false otherwise
   * @since 7.0.6
   */
  public static boolean isDoubleDollarPrefixed(final String line,
                                               final boolean allowedWhitespaces) {
    if (allowedWhitespaces) {
      return DIRECTIVE_TWO_DOLLARS_PREFIXED.matcher(line).matches();
    } else {
      return line.startsWith("//$$");
    }
  }

  public static boolean isDollarBlockPrefixed(final String line, final boolean allowedWhitespaces) {
    if (allowedWhitespaces) {
      return DIRECTIVE_SINGLE_DOLLAR_BLOCK_PREFIXED.matcher(line).matches();
    } else {
      return line.startsWith("//$\"\"\"");
    }
  }

  public static boolean isDoubleDollarBlockPrefixed(final String line,
                                                    final boolean allowedWhitespaces) {
    if (allowedWhitespaces) {
      return DIRECTIVE_SINGLE_DOLLAR_BLOCK_PREFIXED.matcher(line).matches();
    } else {
      return line.startsWith("//$$\"\"\"");
    }
  }

  /**
   * Check that text line starts with single dollar chars
   *
   * @param line               text line to be examined, must not be null
   * @param allowedWhitespaces if true then whitespaces allowed after line comment
   * @return true if the line starts with single dollar chars, false otherwise
   * @since 7.0.6
   */
  public static boolean isSingleDollarPrefixed(final String line,
                                               final boolean allowedWhitespaces) {
    if (allowedWhitespaces) {
      return DIRECTIVE_SINGLE_DOLLAR_PREFIXED.matcher(line).matches();
    } else {
      return line.startsWith("//$");
    }
  }

  /**
   * Allows to check that a text line can be considered as a JCP directive or special line.
   *
   * @param line               text line to be examined
   * @param allowedWhitespaces if true then whitespaces allowed after line comment
   * @return true if the line can be considered as JCP one, false otherwise
   * @since 7.0.6
   */
  public static boolean isJcpCommentLine(final String line, final boolean allowedWhitespaces) {
    return isJcpDirectiveLine(line, allowedWhitespaces)
        || isSingleDollarPrefixed(line, allowedWhitespaces);
  }

  /**
   * Check that a text line contains comment directive.
   *
   * @param line             string to be examined
   * @param allowWhitespaces flag to allow spaces betwee hash and started comment chars
   * @return true if the line contains a directive, false otherwise
   * @since 7.0.6
   */
  public static boolean isJcpDirectiveLine(final String line, final boolean allowWhitespaces) {
    if (allowWhitespaces) {
      return DIRECTIVE_HASH_PREFIXED.matcher(line).matches();
    } else {
      return line.startsWith(AbstractDirectiveHandler.DIRECTIVE_PREFIX);
    }
  }

  public void setTargetFolder(final String folder) {
    this.targetFolder = requireNonNull(folder, "Target folder must not be null");
  }

  public void setTargetFileName(final String name) {
    this.targetFileName =
        requireNonNull(name, "Target file name must not be null");
  }

  public String makeTargetFilePathAsString() {
    String folder = this.getTargetFolder();
    if (!folder.isEmpty() &&
        folder.charAt(folder.length() - 1) != File.separatorChar) {
      folder = folder + File.separatorChar;
    }

    return folder + this.getTargetFileName();
  }

  @Override
  public String toString() {
    return String
        .format("%s: source=%s, targetFolder=%s, targetName=%s", this.getClass().getSimpleName(),
            PreprocessorUtils.getFilePath(this.getSourceFile()), this.getTargetFolder(),
            this.getTargetFileName());
  }

  public List<PreprocessingState.ExcludeIfInfo> processGlobalDirectives(
      final PreprocessingState state, final PreprocessorContext context) throws IOException {
    final PreprocessingState preprocessingState =
        state == null ? context.produceNewPreprocessingState(this, 0) : state;
    preprocessingState.setGlobalPhase(true);

    String leftTrimmedString = null;
    try {
      try {
        while (!Thread.currentThread().isInterrupted()) {
          String nonTrimmedProcessingString = preprocessingState.nextLine();

          final Set<PreprocessingFlag> processFlags = preprocessingState.getPreprocessingFlags();

          if (processFlags.contains(PreprocessingFlag.END_PROCESSING) ||
              processFlags.contains(PreprocessingFlag.ABORT_PROCESSING)) {
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
            switch (processDirective(preprocessingState,
                extractHashPrefixedDirective(leftTrimmedString, context), context, true)) {
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
        final PreprocessorException pp =
            PreprocessorException.extractPreprocessorException(unexpected);
        if (pp == null) {
          throw preprocessingState
              .makeException("Unexpected exception detected", leftTrimmedString, unexpected);
        } else {
          throw pp;
        }
      }
      if (!preprocessingState.isIfStackEmpty()) {
        final TextFileDataContainer lastIf = requireNonNull(preprocessingState.peekIf());
        throw new PreprocessorException(
            "Unclosed " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + "_if instruction detected",
            "", new FilePositionInfo[] {
            new FilePositionInfo(lastIf.getFile(), lastIf.getNextStringIndex())}, null);
      }

      return preprocessingState.popAllExcludeIfInfoData();
    } finally {
      preprocessingState.setGlobalPhase(false);
    }
  }

  private boolean isHashPrefixed(final String line, final PreprocessorContext context) {
    final boolean allowedWhitespaces = context.isAllowWhitespaces();
    final boolean result = isJcpDirectiveLine(line, allowedWhitespaces);
    if (!allowedWhitespaces) {
      if (context.getPreprocessingState().isGlobalPhase() && !result && line.startsWith("// ") &&
          DIRECTIVE_HASH_PREFIXED.matcher(line).matches()) {
        final TextFileDataContainer textContainer =
            context.getPreprocessingState().getCurrentIncludeFileContainer();
        String lineInfo = "<NONE>";
        if (textContainer != null) {
          lineInfo = String.format("%s:%d)", textContainer.getFile().getAbsolutePath(),
              textContainer.getNextStringIndex());
        }
        context.logWarning(WARNING_SPACE_BEFORE_HASH + lineInfo);
      }
    }
    return result;
  }


  private String extractHashPrefixedDirective(final String line,
                                              final PreprocessorContext context) {
    if (context.isAllowWhitespaces()) {
      final Matcher matcher = DIRECTIVE_HASH_PREFIXED.matcher(line);
      if (matcher.find()) {
        return matcher.group(1);
      } else {
        throw new IllegalStateException(
            "Unexpected situation, directive is not found, contact developer! (" + line + ')');
      }
    } else {
      return PreprocessorUtils.extractTail(AbstractDirectiveHandler.DIRECTIVE_PREFIX, line);
    }
  }


  private String extractDoubleDollarPrefixedDirective(final String line,
                                                      final boolean block,
                                                      final PreprocessorContext context) {
    String tail;
    if (context.isAllowWhitespaces()) {
      final Matcher matcher = block ? DIRECTIVE_TWO_DOLLARS_BLOCK_PREFIXED.matcher(line) :
          DIRECTIVE_TWO_DOLLARS_PREFIXED.matcher(line);
      if (matcher.find()) {
        tail = matcher.group(1);
      } else {
        throw new IllegalStateException(
            "Unexpected situation, '//$$' directive is not found, contact developer! (" + line +
                ')');
      }
    } else {
      if (block) {
        tail = PreprocessorUtils.extractTail("//$$\"\"\"", line);
      } else {
        tail = PreprocessorUtils.extractTail("//$$", line);
      }
    }

    if (context.isPreserveIndents()) {
      tail = PreprocessorUtils.replacePartByChar(line, ' ', 0, line.length() - tail.length());
    }
    return tail;
  }


  private String extractSingleDollarPrefixedDirective(final String line,
                                                      final boolean block,
                                                      final PreprocessorContext context) {
    String tail;
    if (context.isAllowWhitespaces()) {
      final Matcher matcher = block ? DIRECTIVE_SINGLE_DOLLAR_BLOCK_PREFIXED.matcher(line) :
          DIRECTIVE_SINGLE_DOLLAR_PREFIXED.matcher(line);
      if (matcher.find()) {
        tail = matcher.group(1);
      } else {
        throw new IllegalStateException(
            "Unexpected situation, '//$' directive is not found, contact developer! (" + line +
                ')');
      }
    } else {
      if (block) {
        tail = PreprocessorUtils.extractTail("//$\"\"\"", line);
      } else {
        tail = PreprocessorUtils.extractTail("//$", line);
      }
    }

    if (context.isPreserveIndents()) {
      tail = PreprocessorUtils.replacePartByChar(line, ' ', 0, line.length() - tail.length());
    }
    return tail;
  }

  private void flushTextBufferForRemovedComments(final StringBuilder textBuffer,
                                                 final ResetablePrinter resetablePrinter,
                                                 final PreprocessingState state,
                                                 final PreprocessorContext context)
      throws IOException {
    if (textBuffer.length() > 0) {
      final List<CommentTextProcessor> processors = context.getCommentTextProcessors();
      final String origText = textBuffer.toString();
      textBuffer.setLength(0);
      String text = origText;

      if (!processors.isEmpty()) {
        processors.forEach(x -> {
          try {
            final String result = x.onUncommentText(origText, this, context, state);
            textBuffer.append(result);
          } catch (Exception ex) {
            throw new PreprocessorException(
                "Error during external comment text processor call",
                origText, state.makeIncludeStack(), ex);
          }
        });

        text = textBuffer.toString();
        textBuffer.setLength(0);
      }
      resetablePrinter.print(text);
    }
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
  public PreprocessingState preprocessFile(final PreprocessingState state,
                                           final PreprocessorContext context) throws IOException {
    // do not clear local variables for cloned context to keep them in the new context
    if (!context.isCloned()) {
      context.clearLocalVariables();
    }

    final PreprocessingState preprocessingState;
    if (state == null) {
      preprocessingState = context.produceNewPreprocessingState(this, 1);
    } else {
      preprocessingState = state;
    }

    String leftTrimmedString = null;

    TextFileDataContainer lastTextFileDataContainer = null;
    final StringBuilder textBlockBuffer = new StringBuilder();

    try {
      while (!Thread.currentThread().isInterrupted()) {
        final ResetablePrinter thePrinter =
            requireNonNull(preprocessingState.getPrinter(), "Printer must be defined");

        String rawString = preprocessingState.nextLine();
        final boolean presentedNextLine = preprocessingState.hasReadLineNextLineInEnd();

        final Set<PreprocessingFlag> processFlags = preprocessingState.getPreprocessingFlags();

        if (processFlags.contains(PreprocessingFlag.END_PROCESSING) ||
            processFlags.contains(PreprocessingFlag.ABORT_PROCESSING)) {
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
          this.flushTextBufferForRemovedComments(textBlockBuffer, thePrinter, state, context);
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
          this.flushTextBufferForRemovedComments(textBlockBuffer, thePrinter, state, context);
          final String extractedDirective =
              extractHashPrefixedDirective(stringToBeProcessed, context);
          switch (processDirective(preprocessingState, extractedDirective, context, false)) {
            case PROCESSED:
            case READ_NEXT_LINE: {
              if (context.isKeepLines()) {
                final String text = stringPrefix +
                    AbstractDirectiveHandler.PREFIX_FOR_KEEPING_LINES_PROCESSED_DIRECTIVES +
                    extractedDirective;
                if (doPrintLn) {
                  thePrinter.println(text, context.getEol());
                } else {
                  thePrinter.print(text);
                }
              }
              continue;
            }
            case SHOULD_BE_COMMENTED: {
              final String text = stringPrefix +
                  AbstractDirectiveHandler.PREFIX_FOR_KEEPING_LINES_PROCESSED_DIRECTIVES +
                  extractedDirective;
              if (doPrintLn) {
                thePrinter.println(text, context.getEol());
              } else {
                thePrinter.print(text);
              }
              continue;
            }
            default:
              throw new IllegalStateException("Unsupported result");
          }
        }

        if (preprocessingState.isDirectiveCanBeProcessed() &&
            !preprocessingState.getPreprocessingFlags()
                .contains(PreprocessingFlag.TEXT_OUTPUT_DISABLED)) {
          final boolean startsWithTwoDollars =
              isDoubleDollarPrefixed(leftTrimmedString, context.isAllowWhitespaces());

          if (!startsWithTwoDollars) {
            stringToBeProcessed = PreprocessorUtils.processMacroses(leftTrimmedString, context);
          }

          if (startsWithTwoDollars) {
            // Output the tail of the string to the output stream without comments and macroses
            final String text =
                extractDoubleDollarPrefixedDirective(leftTrimmedString, false, context);
            if (context.isAllowsBlocks() &&
                isDoubleDollarBlockPrefixed(leftTrimmedString, context.isAllowWhitespaces())) {
              textBlockBuffer.append(
                  extractDoubleDollarPrefixedDirective(leftTrimmedString, true, context));
              if (doPrintLn) {
                textBlockBuffer.append(context.getEol());
              }
            } else {
              this.flushTextBufferForRemovedComments(textBlockBuffer, thePrinter, state, context);
              textBlockBuffer.append(stringPrefix).append(text);
              if (doPrintLn) {
                textBlockBuffer.append(context.getEol());
              }
              this.flushTextBufferForRemovedComments(textBlockBuffer, thePrinter, state, context);
            }
          } else if (isSingleDollarPrefixed(stringToBeProcessed, context.isAllowWhitespaces())) {
            // Output the tail of the string to the output stream without comments
            final String text =
                extractSingleDollarPrefixedDirective(stringToBeProcessed, false, context);

            if (context.isAllowsBlocks() &&
                isDollarBlockPrefixed(stringToBeProcessed, context.isAllowWhitespaces())) {
              textBlockBuffer.append(
                  extractSingleDollarPrefixedDirective(stringToBeProcessed, true, context));
              if (doPrintLn) {
                textBlockBuffer.append(context.getEol());
              }
            } else {
              this.flushTextBufferForRemovedComments(textBlockBuffer, thePrinter, state, context);
              textBlockBuffer.append(stringPrefix).append(text);
              if (doPrintLn) {
                textBlockBuffer.append(context.getEol());
              }
              this.flushTextBufferForRemovedComments(textBlockBuffer, thePrinter, state, context);
            }
          } else {
            // Just string
            this.flushTextBufferForRemovedComments(textBlockBuffer, thePrinter, state, context);

            final String strToOut = findTailRemover(stringToBeProcessed, context);

            if (preprocessingState.getPreprocessingFlags()
                .contains(PreprocessingFlag.COMMENT_NEXT_LINE)) {
              thePrinter.print(AbstractDirectiveHandler.ONE_LINE_COMMENT);
              preprocessingState.getPreprocessingFlags()
                  .remove(PreprocessingFlag.COMMENT_NEXT_LINE);
            }

            thePrinter.print(stringPrefix);
            if (doPrintLn) {
              thePrinter.println(strToOut, context.getEol());
            } else {
              thePrinter.print(strToOut);
            }
          }
        } else if (context.isKeepLines()) {
          flushTextBufferForRemovedComments(textBlockBuffer, thePrinter, state, context);
          final String text = AbstractDirectiveHandler.PREFIX_FOR_KEEPING_LINES + rawString;
          if (doPrintLn) {
            thePrinter.println(text, context.getEol());
          } else {
            thePrinter.print(text);
          }
        }
      }
    } catch (Exception unexpected) {
      final String message =
          unexpected.getMessage() == null ? "Unexpected exception" : unexpected.getMessage();
      throw preprocessingState.makeException(message, leftTrimmedString, unexpected);
    }

    if (!preprocessingState.isIfStackEmpty()) {
      final TextFileDataContainer lastIf =
          requireNonNull(preprocessingState.peekIf(), "'IF' stack is empty");
      throw new PreprocessorException(
          "Unclosed " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + "if instruction detected",
          "", new FilePositionInfo[] {
          new FilePositionInfo(lastIf.getFile(), lastIf.getNextStringIndex())}, null);
    }
    if (!preprocessingState.isWhileStackEmpty()) {
      final TextFileDataContainer lastWhile =
          requireNonNull(preprocessingState.peekWhile(), "'WHILE' stack is empty");
      throw new PreprocessorException(
          "Unclosed " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + "while instruction detected",
          "", new FilePositionInfo[] {
          new FilePositionInfo(lastWhile.getFile(), lastWhile.getNextStringIndex())}, null);
    }

    if (!context.isDryRun() && requireNonNull(lastTextFileDataContainer).isAutoFlush()) {
      final File outFile = context.createDestinationFileForPath(makeTargetFilePathAsString());

      final boolean wasSaved =
          preprocessingState.saveBuffersToFile(outFile, context.getKeepComments());

      if (context.isVerbose()) {
        context.logForVerbose(String
            .format("Content was %s into file '%s'", (wasSaved ? "saved" : "not saved"),
                outFile));
      }

      if (this.sourceFile != null && context.isKeepAttributes() &&
          !PreprocessorUtils.copyFileAttributes(this.getSourceFile(), outFile)) {
        throw new IOException("Can't copy attributes in result file: " + outFile);
      }

      this.getGeneratedResources().add(outFile);
    }
    return preprocessingState;
  }

  private boolean checkDirectiveArgumentRoughly(final AbstractDirectiveHandler directive,
                                                final String rest) {
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


  protected AfterDirectiveProcessingBehaviour processDirective(final PreprocessingState state,
                                                               final String directiveString,
                                                               final PreprocessorContext context,
                                                               final boolean firstPass) {
    final boolean executionEnabled = state.isDirectiveCanBeProcessed();

    for (final AbstractDirectiveHandler handler : context.getDirectiveHandlers()) {
      final String name = handler.getName();
      if (directiveString.startsWith(name)) {
        if ((firstPass && !handler.isGlobalPhaseAllowed()) ||
            (!firstPass && !handler.isPreprocessingPhaseAllowed())) {
          return AfterDirectiveProcessingBehaviour.READ_NEXT_LINE;
        }

        final boolean allowedForExecution =
            executionEnabled || !handler.executeOnlyWhenExecutionAllowed();

        final String restOfString = PreprocessorUtils.extractTail(name, directiveString);
        if (checkDirectiveArgumentRoughly(handler, restOfString)) {
          if (allowedForExecution) {
            return handler.execute(restOfString, context);
          } else {
            return context.isKeepLines() ? AfterDirectiveProcessingBehaviour.SHOULD_BE_COMMENTED :
                AfterDirectiveProcessingBehaviour.PROCESSED;
          }
        } else {
          throw context.makeException(
              "Detected bad argument for " + AbstractDirectiveHandler.DIRECTIVE_PREFIX +
                  handler.getName(), null);
        }
      }
    }
    throw context.makeException("Unknown preprocessor directive [" + directiveString + ']', null);
  }

  public void setExcluded(final boolean flag) {
    excludedFromPreprocessing = flag;
  }
}
