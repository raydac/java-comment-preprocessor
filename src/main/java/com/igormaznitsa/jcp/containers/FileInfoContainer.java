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
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.igormaznitsa.jcp.utils.ResetablePrinter;
import com.igormaznitsa.meta.annotation.MustNotContainNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.igormaznitsa.meta.common.utils.Assertions.assertNotNull;

/**
 * The class is one from the main classes in the preprocessor because it describes a preprocessing file and contains business logic for the process
 *
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
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
  private final boolean forCopyOnly;

  /**
   * The flag shows that the file has been excluded from preprocessing and it will not be preprocessed and copied
   */
  private boolean excludedFromPreprocessing;

  /**
   * The destination directory for the file
   */
  private String destFolder;

  /**
   * The destination name for the file
   */
  private String destFileName;

  @Nonnull
  public File getSourceFile() {
    return sourceFile;
  }

  public boolean isExcludedFromPreprocessing() {
    return excludedFromPreprocessing;
  }

  public boolean isForCopyOnly() {
    return forCopyOnly;
  }

  @Nonnull
  public String getDestinationDir() {
    return destFolder;
  }

  @Nonnull
  public String getDestinationName() {
    return destFileName;
  }

  public FileInfoContainer(@Nonnull final File srcFile, @Nonnull final String dstFileName, final boolean copyOnly) {
    assertNotNull("The source file is null", srcFile);
    assertNotNull("The destination file name is null", dstFileName);

    forCopyOnly = copyOnly;
    excludedFromPreprocessing = false;
    sourceFile = srcFile;

    int lastDirSeparator = dstFileName.lastIndexOf('/');
    if (lastDirSeparator < 0) {
      lastDirSeparator = dstFileName.lastIndexOf('\\');
    }

    if (lastDirSeparator < 0) {
      destFolder = "." + File.separatorChar;
      destFileName = dstFileName;
    } else {
      destFolder = dstFileName.substring(0, lastDirSeparator);
      destFileName = dstFileName.substring(lastDirSeparator + 1);
    }
  }

  @Nonnull
  public String getDestinationFilePath() {
    String dir = this.destFolder;
    if (!dir.isEmpty() && dir.charAt(dir.length() - 1) != File.separatorChar) {
      dir = dir + File.separatorChar;
    }

    return dir + destFileName;
  }

  @Override
  @Nonnull
  public String toString() {
    return "FileInfoContainer: source=" + PreprocessorUtils.getFilePath(sourceFile) + " destFolder=" + destFolder + " destFile=" + destFileName;
  }

  @Nonnull
  @MustNotContainNull
  public List<PreprocessingState.ExcludeIfInfo> processGlobalDirectives(@Nullable final PreprocessingState state, @Nonnull final PreprocessorContext context) throws IOException {
    final PreprocessingState preprocessingState = state == null ? context.produceNewPreprocessingState(this, 0) : state;

    String leftTrimmedString = null;
    try {
      while (true) {
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

        if (isHashPrefixed(leftTrimmedString,context)) {
          switch (processDirective(preprocessingState, extractHashPrefixedDirective(leftTrimmedString,context), context, true)) {
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
          "", new FilePositionInfo[]{new FilePositionInfo(lastIf.getFile(), lastIf.getNextStringIndex())}, null);
    }

    return preprocessingState.popAllExcludeIfInfoData();
  }

  private boolean isDoubleDollarPrefixed(@Nonnull final String line, @Nonnull final PreprocessorContext context) {
    if (context.isAllowWhitespace()) {
      return DIRECTIVE_TWO_DOLLARS_PREFIXED.matcher(line).matches();
    } else {
      return line.startsWith("//$$");
    }
  }

  private boolean isSingleDollarPrefixed(@Nonnull final String line, @Nonnull final PreprocessorContext context) {
    if (context.isAllowWhitespace()) {
      return DIRECTIVE_SINGLE_DOLLAR_PREFIXED.matcher(line).matches();
    } else {
      return line.startsWith("//$");
    }
  }

  private boolean isHashPrefixed(@Nonnull final String line, @Nonnull final PreprocessorContext context) {
    if (context.isAllowWhitespace()) {
      return DIRECTIVE_HASH_PREFIXED.matcher(line).matches();
    } else {
      return line.startsWith(AbstractDirectiveHandler.DIRECTIVE_PREFIX);
    }
  }

  @Nonnull
  private String extractHashPrefixedDirective(@Nonnull final String line, @Nonnull final PreprocessorContext context) {
    if (context.isAllowWhitespace()) {
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
    if (context.isAllowWhitespace()) {
      final Matcher matcher = DIRECTIVE_TWO_DOLLARS_PREFIXED.matcher(line);
      if (matcher.find()) {
        tail = matcher.group(1);
      } else {
        throw new Error("Unexpected situation, '//$$' directive is not found, contact developer! (" + line + ')');
      }
    } else {
      tail = PreprocessorUtils.extractTail("//$$", line);
    }

    if (context.isPreserveIndent()) {
      tail = PreprocessorUtils.replacePartByChar(line, ' ', 0, line.length() - tail.length());
    }
    return tail;
  }

  @Nonnull
  private String extractSingleDollarPrefixedDirective(@Nonnull final String line, @Nonnull final PreprocessorContext context) {
    String tail;
    if (context.isAllowWhitespace()) {
      final Matcher matcher = DIRECTIVE_SINGLE_DOLLAR_PREFIXED.matcher(line);
      if (matcher.find()) {
        tail = matcher.group(1);
      } else {
        throw new Error("Unexpected situation, '//$' directive is not found, contact developer! (" + line + ')');
      }
    } else {
      tail = PreprocessorUtils.extractTail("//$", line);
    }
    
    if (context.isPreserveIndent()) {
      tail = PreprocessorUtils.replacePartByChar(line, ' ', 0, line.length() - tail.length());
    }
    return tail;
  }

  /**
   * Preprocess file, NB! it doesn't clear local variables automatically for cloned contexts
   *
   * @param state the start preprocessing state, can be null
   * @param context the preprocessor context, must not be null
   * @return the state for the preprocessed file
   * @throws IOException it will be thrown for IO errors
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
      while (true) {
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

        final boolean usePrintLn = presentedNextLine || !context.isCareForLastNextLine();

        if (isHashPrefixed(stringToBeProcessed, context)) {
          final String extractedDirective = extractHashPrefixedDirective(stringToBeProcessed, context);
          switch (processDirective(preprocessingState, extractedDirective, context, false)) {
            case PROCESSED:
            case READ_NEXT_LINE: {
              if (context.isKeepLines()) {
                final String text = stringPrefix + AbstractDirectiveHandler.PREFIX_FOR_KEEPING_LINES_PROCESSED_DIRECTIVES + extractedDirective;
                final ResetablePrinter thePrinter = assertNotNull(preprocessingState.getPrinter());
                if (usePrintLn) {
                  thePrinter.println(text);
                } else {
                  thePrinter.print(text);
                }
              }
              continue;
            }
            case SHOULD_BE_COMMENTED: {
              final String text = stringPrefix + AbstractDirectiveHandler.PREFIX_FOR_KEEPING_LINES_PROCESSED_DIRECTIVES + extractedDirective;
              final ResetablePrinter thePrinter = assertNotNull(preprocessingState.getPrinter());
              if (usePrintLn) {
                thePrinter.println(text);
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
            if (usePrintLn) {
              thePrinter.println(text);
            } else {
              thePrinter.print(text);
            }
          } else if (isSingleDollarPrefixed(stringToBeProcessed, context)) {
            // Output the tail of the string to the output stream without comments
            thePrinter.print(stringPrefix);

            final String text = extractSingleDollarPrefixedDirective(stringToBeProcessed, context);

            if (usePrintLn) {
              thePrinter.println(text);
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
            if (usePrintLn) {
              thePrinter.println(strToOut);
            } else {
              thePrinter.print(strToOut);
            }
          }
        } else if (context.isKeepLines()) {
          final String text = AbstractDirectiveHandler.PREFIX_FOR_KEEPING_LINES + rawString;
          if (usePrintLn) {
            thePrinter.println(text);
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
          "", new FilePositionInfo[]{new FilePositionInfo(lastIf.getFile(), lastIf.getNextStringIndex())}, null);
    }
    if (!preprocessingState.isWhileStackEmpty()) {
      final TextFileDataContainer lastWhile = assertNotNull("'WHILE' stack is empty", preprocessingState.peekWhile());
      throw new PreprocessorException("Unclosed " + AbstractDirectiveHandler.DIRECTIVE_PREFIX + "while instruction detected",
          "", new FilePositionInfo[]{new FilePositionInfo(lastWhile.getFile(), lastWhile.getNextStringIndex())}, null);
    }

    if (!context.isFileOutputDisabled() && assertNotNull(lastTextFileDataContainer).isAutoFlush()) {
      final File outFile = context.createDestinationFileForPath(getDestinationFilePath());
      
      final boolean wasSaved = preprocessingState.saveBuffersToFile(outFile, context.isRemoveComments());

      if (context.isVerbose()) {
        context.logForVerbose("Content was " + (wasSaved ? "saved" : "not saved") + " into file '" + outFile + "\'");
      }

      if (this.sourceFile!=null && context.isCopyFileAttributes()) {
        PreprocessorUtils.copyFileAttributes(this.sourceFile, outFile);
      }
    }
    return preprocessingState;
  }

  @Nonnull
  private static String findTailRemover(@Nonnull final String str, @Nonnull final PreprocessorContext context) {
    String result = str;
    if (context.isAllowWhitespace()) {
      final Matcher matcher = DIRECTIVE_TAIL_REMOVER.matcher(str);
      if (matcher.find()){
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

    for (final AbstractDirectiveHandler handler : AbstractDirectiveHandler.DIRECTIVES) {
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

  public void setDestinationDir(@Nonnull final String destDir) {
    assertNotNull("String is null", destDir);
    destFolder = destDir;
  }

  public void setDestinationName(@Nonnull final String destName) {
    assertNotNull("String is null", destName);
    destFileName = destName;
  }

  public void setExcluded(final boolean flag) {
    excludedFromPreprocessing = flag;
  }
}
