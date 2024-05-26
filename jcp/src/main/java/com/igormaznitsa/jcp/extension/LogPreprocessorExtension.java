package com.igormaznitsa.jcp.extension;


import com.igormaznitsa.jcp.containers.TextFileDataContainer;
import com.igormaznitsa.jcp.context.PreprocessorContext;
import com.igormaznitsa.jcp.expression.Value;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Preprocessor extension which just make info logging for arguments of action calls and also
 * can log user function calls, their arity should be provided in the end of function name like '$hello2(1,2)'
 *
 * @since 7.1.2
 */
public class LogPreprocessorExtension implements PreprocessorExtension {

  private static final Pattern PATTERN = Pattern.compile("^(\\D+)(\\d+)?$");

  private static String findPosition(final PreprocessorContext context) {
    if (context == null) {
      return "";
    }
    if (context.getPreprocessingState().getCurrentIncludeStack().isEmpty()) {
      return "";
    } else {
      final TextFileDataContainer dataContainer =
          context.getPreprocessingState().getCurrentIncludeStack().get(0);
      return (dataContainer.getFile() == null ? "<unknown>" : dataContainer.getFile().getName())
          + ':'
          + (dataContainer.getLastReadStringIndex() + 1);
    }
  }

  @Override
  public boolean processAction(final PreprocessorContext context, final Value[] parameters) {
    context.logInfo(String.format("Called action: %s at %s", Arrays.toString(parameters),
        findPosition(context)));
    return true;
  }

  @Override
  public Value processUserFunction(final PreprocessorContext context, final String functionName,
                                   final Value[] arguments) {
    context.logInfo(
        String.format("Called user function '%s'(%s) at %s",
            functionName, Arrays.toString(arguments),
            findPosition(context)));
    return Value.BOOLEAN_TRUE;
  }

  @Override
  public int getUserFunctionArity(final String functionName) {
    final Matcher matcher = PATTERN.matcher(functionName);
    if (matcher.find() && matcher.group(2) != null) {
      return Integer.parseInt(matcher.group(2));
    }
    return 0;
  }
}
